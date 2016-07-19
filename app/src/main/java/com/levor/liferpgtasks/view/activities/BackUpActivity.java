package com.levor.liferpgtasks.view.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxOAuth1AccessToken;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.android.AuthActivity;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListRevisionsResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.levor.liferpgtasks.LifeRPGApplication;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.FileUtils;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;
import com.levor.liferpgtasks.factories.DropboxClientFactory;

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

    private boolean syncDropbox = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifeController = LifeController.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = lifeController.getSharedPreferences();
        if (prefs.getBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false) || syncDropbox) {
            String accessToken = prefs.getString(LifeController.DROPBOX_ACCESS_TOKEN_TAG, null);
            if (accessToken == null) {
                accessToken = Auth.getOAuth2Token();
                if (accessToken != null) {
                    prefs.edit().putString(LifeController.DROPBOX_ACCESS_TOKEN_TAG, accessToken).apply();
                    initAndLoadData(accessToken);
                } else {
                    authorizeToDropbox();
                }
            } else {
                initAndLoadData(accessToken);
            }
        }
        syncDropbox = false;
    }

    private void initAndLoadData(String accessToken) {
        DropboxClientFactory.init(accessToken);
        if (lifeController.isDropBoxAutoBackupEnabled()) {
            checkAndBackupToDropBox(true);
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
        lifeController.getSharedPreferences().edit().putString(LifeController.DROPBOX_ACCESS_TOKEN_TAG, null).apply();
        Observable<String> backUpObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(getString(R.string.authorization_required));
                }
                Auth.startOAuth2Authentication(BackUpActivity.this, getString(R.string.dropbox_app_key));
            }
        });
        Subscription s = backUpObservable.subscribeOn(Schedulers.io())
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
                    FileMetadata metadata = DropboxClientFactory.getClient().files().uploadBuilder(DB_ADDRESS_IN_DROPBOX)
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(inputStream);
                    SharedPreferences prefs = lifeController.getSharedPreferences();
                    prefs.edit().putString(LAST_LOADED_DB_REVISION_TAG, metadata.getRev()).apply();

                    inputStream.close();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (InvalidAccessTokenException | IllegalStateException e) {
                    authorizeToDropbox();
                    syncDropbox = true;
                } catch (DbxException | IOException e) {
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
                        if (!silent) {
                            Toast.makeText(BackUpActivity.this, R.string.db_exported, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                        if (!silent) {
                            Toast.makeText(BackUpActivity.this, R.string.dropbox_backup_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (!silent) {
                            Toast.makeText(BackUpActivity.this, s, Toast.LENGTH_SHORT).show();
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
                    FileMetadata metadata = DropboxClientFactory.getClient().files().downloadBuilder(DB_ADDRESS_IN_DROPBOX)
                            .download(outputStream);
                    SharedPreferences prefs = getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                    prefs.edit().putString(LAST_LOADED_DB_REVISION_TAG, metadata.getRev()).apply();

                    File oldDB = getDatabasePath(DataBaseHelper.DATABASE_NAME);
                    if (tempDB.exists()) {
                        FileUtils.copyFile(new FileInputStream(tempDB), new FileOutputStream(oldDB));
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(getString(R.string.db_imported));
                            subscriber.onCompleted();
                        }
                    }
                } catch (InvalidAccessTokenException | IllegalStateException e) {
                    authorizeToDropbox();
                    syncDropbox = true;
                } catch (IOException | DbxException e){
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
                        Toast.makeText(BackUpActivity.this, R.string.db_import_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(BackUpActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
        subscriptions.add(s);
    }

    public String getDropboxFileRevision() {
        try {
            ListRevisionsResult result = DropboxClientFactory.getClient().files().listRevisions(DB_ADDRESS_IN_DROPBOX);
            return result.getEntries().get(0).getRev();
        } catch (InvalidAccessTokenException | IllegalStateException e) {
            authorizeToDropbox();
            syncDropbox = true;
            return null;
        } catch (DbxException e) {
            return null;
        }
    }

    //
    public void checkAndBackupToDropBox(final boolean silent) {
        Subscription s = Observable.create(new Observable.OnSubscribe<ExportActions>() {
            @Override
            public void call(Subscriber<? super ExportActions> subscriber) {
                SharedPreferences prefs = lifeController.getSharedPreferences();

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
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ExportActions exportActionsEnum) {
                        switch (exportActionsEnum) {
                            case BACKUP_TO_DROPBOX:
                                backUpDBToDropBox(silent);
                                break;
                            case REWRITING_DB_DIALOG:
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
                            case DROPBOX_ALREADY_CONTAINS_DIALOG:
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


    public void checkAndImportFromDropBox() {
        Subscription s = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
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
                        syncDropbox = true;
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

    public void onDBFileUpdated(boolean isFileDeleted) {
    }

    protected LifeRPGApplication getCurrentApplication() {
        return (LifeRPGApplication) getApplication();
    }

//    private DropboxAPI<AndroidAuthSession> getDBApi() {
//        return getCurrentApplication().getDBApi();
//    }

    enum ExportActions {BACKUP_TO_DROPBOX, REWRITING_DB_DIALOG, DROPBOX_ALREADY_CONTAINS_DIALOG}
}
