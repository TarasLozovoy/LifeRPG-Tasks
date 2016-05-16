package com.levor.liferpgtasks.view.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.levor.liferpgtasks.LifeRPGApplication;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.FileUtils;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BackUpActivity extends ActionBarActivity {
    private final static String LAST_LOADED_DB_REVISION_TAG = "last_loaded_db_revision";

    private final static String DB_ADDRESS_IN_DROPBOX = "/LifeRPGTasksDB.db";
    private final static String TEMP_DB_FILE_NAME = "temporaryDB.db";

    protected LifeController lifeController;
    private List<Subscription> subscriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifeController = LifeController.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getDBApi().getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                getDBApi().getSession().finishAuthentication();

                String accessToken = getDBApi().getSession().getOAuth2AccessToken();
                SharedPreferences prefs = this.getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                prefs.edit().putString(LifeController.DROPBOX_ACCESS_TOKEN_TAG, accessToken).apply();
                if (lifeController.isDropBoxAutoBackupEnabled()){
                    checkAndBackupToDropBox(true);
                }
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Subscription s : subscriptions) {
            if (!s.isUnsubscribed()) {
                s.unsubscribe();
            }
        }
    }

    public void authorizeToDropbox() {
        Subscription s = Observable.just(getString(R.string.authorization_required))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(BackUpActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                });
        subscriptions.add(s);
        getDBApi().getSession().startOAuth2Authentication(BackUpActivity.this);
    }

    private void backUpDBToDropBox(final boolean silent) {
        Observable<String> backUpObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(getString(R.string.db_exporting));
                    }
                    File db = getDatabasePath(DataBaseHelper.DATABASE_NAME);
                    FileInputStream inputStream = new FileInputStream(db);
                    DropboxAPI.Entry response = getDBApi().putFileOverwrite(DB_ADDRESS_IN_DROPBOX, inputStream,
                            db.length(), null);
                    SharedPreferences prefs = getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                    prefs.edit().putString(LAST_LOADED_DB_REVISION_TAG, response.rev).apply();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (DropboxException | IOException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
        Subscription s = backUpObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                        Toast.makeText(BackUpActivity.this, R.string.db_exported, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                        if (!silent) {
                            Toast.makeText(BackUpActivity.this, R.string.dropbox_backup_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (!silent) {
                            Toast.makeText(BackUpActivity.this, s, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        subscriptions.add(s);
    }

    private void importFromDropBox() {
        Observable<String> backUpObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                File tempDB = null;
                try {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(getString(R.string.db_importing));
                    }
                    tempDB = new File(getCacheDir(), TEMP_DB_FILE_NAME);
                    FileOutputStream outputStream = new FileOutputStream(tempDB);
                    DropboxAPI.DropboxFileInfo info = getDBApi().getFile(DB_ADDRESS_IN_DROPBOX, null, outputStream, null);
                    SharedPreferences prefs = getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                    prefs.edit().putString(LAST_LOADED_DB_REVISION_TAG, info.getMetadata().rev).apply();

                    File oldDB = getDatabasePath(DataBaseHelper.DATABASE_NAME);
                    if (tempDB.exists()) {
                        FileUtils.copyFile(new FileInputStream(tempDB), new FileOutputStream(oldDB));
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(getString(R.string.db_imported));
                            subscriber.onCompleted();
                        }
                    }
                } catch (IOException | DropboxException e){
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                } finally {
                    lifeController.openDBConnection();
                    if (tempDB != null) {
                        tempDB.delete();
                    }
                }
            }
        });
        Subscription s = backUpObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                        lifeController.openDBConnection();
                        onDBFileUpdated(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                        Toast.makeText(BackUpActivity.this, R.string.db_import_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(BackUpActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                });
        subscriptions.add(s);
    }

    public String getDropboxFileRevision() {
        try {
            DropboxAPI.Entry existingEntry = getDBApi().metadata(DB_ADDRESS_IN_DROPBOX, 1, null, false, null);
            return existingEntry.rev;
        } catch (DropboxException e) {
            return null;
        }
    }

    public void checkAndBackupToDropBox(final boolean silent) {
        Subscription s = Observable.create(new Observable.OnSubscribe<ExportActions>() {
            @Override
            public void call(Subscriber<? super ExportActions> subscriber) {
                SharedPreferences prefs = lifeController.getSharedPreferences();
                try {
                    getDBApi().accountInfo();
                } catch (DropboxUnlinkedException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                    return;
                } catch (DropboxException ignored) {}

                String lastLoadedFileRev = prefs.getString(LAST_LOADED_DB_REVISION_TAG, null);
                String dropboxFileRev = getDropboxFileRevision();
                if (lastLoadedFileRev != null) {
                    if (lastLoadedFileRev.equals(dropboxFileRev)) {
                        //most common situation, last load to dropbox was performed from current device
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(ExportActions.BACKUP_TO_DROPBOX);
                        }
                    } else {
                        //dropbox was updated from another device
                        if (lifeController.isInternetConnectionActive()) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(ExportActions.REWRITING_DB_DIALOG);
                            }
                        }
                    }
                } else {
                    if (dropboxFileRev == null) {
                        //first load, no files was loaded since now ever.
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(ExportActions.BACKUP_TO_DROPBOX);
                        }
                    } else {
                        //first load to dropbox from current device, dropbox already have DB version
                        if (lifeController.isInternetConnectionActive()) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(ExportActions.DROPBOX_ALREADY_CONTAINS_DIALOG);
                            }
                        }
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExportActions>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        authorizeToDropbox();
                        lifeController.getSharedPreferences()
                                .edit()
                                .putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false)
                                .apply();
                    }

                    @Override
                    public void onNext(ExportActions exportActionsEnum) {
                        switch (exportActionsEnum) {
                            case BACKUP_TO_DROPBOX :
                                backUpDBToDropBox(silent);
                                break;
                            case REWRITING_DB_DIALOG :
                                AlertDialog.Builder alert = new AlertDialog.Builder(BackUpActivity.this);
                                alert.setTitle(R.string.backup_db_to_dropbox_alert_title)
                                        .setCancelable(false)
                                        .setMessage(R.string.backup_db_to_dropbox_conflict)
                                        .setPositiveButton(R.string.import_dropbox, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                importFromDropBox();
                                            }
                                        })
                                        .setNegativeButton(R.string.export_local, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                backUpDBToDropBox(false);
                                            }
                                        })
                                        .show();
                                break;
                            case DROPBOX_ALREADY_CONTAINS_DIALOG :
                                AlertDialog.Builder a = new AlertDialog.Builder(BackUpActivity.this);
                                a.setTitle(R.string.backup_db_to_dropbox_alert_title)
                                        .setCancelable(false)
                                        .setMessage(R.string.backup_db_to_dropbox_already_contains)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                importFromDropBox();
                                            }
                                        })
                                        .setNegativeButton(R.string.backup_db_to_dropbox_no_rewrite, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                backUpDBToDropBox(false);
                                            }
                                        })
                                        .show();
                                break;
                        }
                    }
                });
        subscriptions.add(s);
    }

    public void checkAndImportFromDropBox(){
        Subscription s = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    getDBApi().accountInfo();
                } catch (DropboxUnlinkedException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                    return;
                } catch (DropboxException ignored) {}

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        AlertDialog.Builder alert = new AlertDialog.Builder(BackUpActivity.this);
                        alert.setTitle(R.string.db_import)
                                .setCancelable(false)
                                .setMessage(R.string.importing_dp_from_dropbox)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        importFromDropBox();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        authorizeToDropbox();
                        lifeController.getSharedPreferences()
                                .edit()
                                .putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false)
                                .apply();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
        subscriptions.add(s);
    }

    public void onDBFileUpdated(boolean isFileDeleted){}

    protected LifeRPGApplication getCurrentApplication() {
        return (LifeRPGApplication) getApplication();
    }

    private DropboxAPI<AndroidAuthSession> getDBApi() {
        return getCurrentApplication().getDBApi();
    }

    enum ExportActions {BACKUP_TO_DROPBOX, REWRITING_DB_DIALOG, DROPBOX_ALREADY_CONTAINS_DIALOG}
}
