package com.levor.liferpgtasks.view.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

public class BackUpActivity extends ActionBarActivity {
    private final static String LAST_LOADED_DB_REVISION_TAG = "last_loaded_db_revision";

    private final static String DB_ADDRESS_IN_DROPBOX = "/LifeRPGTasksDB.db";
    private final static String TEMP_DB_FILE_NAME = "temporaryDB.db";

    protected LifeController lifeController;

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

    public void authorizeToDropbox() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BackUpActivity.this, R.string.authorization_required, Toast.LENGTH_LONG).show();
            }
        });
        getDBApi().getSession().startOAuth2Authentication(BackUpActivity.this);
    }

    private void backUpDBToDropBox(final boolean silent) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    File db = getDatabasePath(DataBaseHelper.DATABASE_NAME);
                    FileInputStream inputStream = new FileInputStream(db);
                    DropboxAPI.Entry response = getDBApi().putFileOverwrite(DB_ADDRESS_IN_DROPBOX, inputStream,
                            db.length(), null);
                    SharedPreferences prefs = getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                    prefs.edit().putString(LAST_LOADED_DB_REVISION_TAG, response.rev).apply();
                } catch (IOException e) {
                    //do nothing
                } catch (DropboxException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BackUpActivity.this, R.string.dropbox_backup_error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                if (!silent) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BackUpActivity.this, R.string.db_exporting, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!silent) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BackUpActivity.this, R.string.db_exported, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void importFromDropBox() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    File tempDB = new File(getCacheDir(), TEMP_DB_FILE_NAME);
                    FileOutputStream outputStream = new FileOutputStream(tempDB);
                    DropboxAPI.DropboxFileInfo info = getDBApi().getFile(DB_ADDRESS_IN_DROPBOX, null, outputStream, null);
                    SharedPreferences prefs = getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                    prefs.edit().putString(LAST_LOADED_DB_REVISION_TAG, info.getMetadata().rev).apply();

                    File oldDB = getDatabasePath(DataBaseHelper.DATABASE_NAME);
                    if (tempDB.exists()){
                        try {
                            FileUtils.copyFile(new FileInputStream(tempDB), new FileOutputStream(oldDB));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BackUpActivity.this, getString(R.string.db_imported), Toast.LENGTH_LONG)
                                            .show();
                                    lifeController.openDBConnection();
                                    onDBFileUpdated(false);
                                }
                            });
                        } catch (IOException e){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BackUpActivity.this, getString(R.string.db_import_error), Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        } finally {
                            lifeController.openDBConnection();
                            tempDB.delete();
                        }
                    }
                } catch (IOException e) {
                    //do nothing
                }catch (DropboxException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BackUpActivity.this, R.string.db_import_error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences prefs = getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                try {
                    getDBApi().accountInfo();
                } catch (DropboxUnlinkedException e) {
                    authorizeToDropbox();
                    prefs.edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false).apply();
                    return null;
                } catch (DropboxException ignored) {}

//                prefs.edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, true).apply();
                String lastLoadedFileRev = prefs.getString(LAST_LOADED_DB_REVISION_TAG, null);
                String dropboxFileRev = getDropboxFileRevision();
                if (lastLoadedFileRev != null) {
                    if (lastLoadedFileRev.equals(dropboxFileRev)) {
                        //most common situation, last load to dropbox was performed from current device
                        backUpDBToDropBox(silent);
                    } else {
                        //dropbox was updated from another device
                        if (lifeController.isInternetConnectionActive()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showRewritingDBDialog();
                                }
                            });
                        }
                    }
                } else {
                    if (dropboxFileRev == null) {
                        //first load, no files was loaded since now ever.
                        backUpDBToDropBox(silent);
                    } else {
                        //first load to dropbox from current device, dropbox already have DB version
                        if (lifeController.isInternetConnectionActive()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showDropBoxAlreadyContainsDialog();
                                }
                            });
                        }
                    }
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void checkAndImportFromDropBox(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences prefs = getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                try {
                    getDBApi().accountInfo();
                } catch (DropboxUnlinkedException e) {
                    authorizeToDropbox();
                    prefs.edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false).apply();
                    return null;
                } catch (DropboxException ignored) {}

//                prefs.edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, true).apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                });
                return null;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void showDropBoxAlreadyContainsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.backup_db_to_dropbox_alert_title)
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
    }

    private void showRewritingDBDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
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
    }

    public void onDBFileUpdated(boolean isFileDeleted){}

    protected LifeRPGApplication getCurrentApplication() {
        return (LifeRPGApplication) getApplication();
    }

    private DropboxAPI<AndroidAuthSession> getDBApi() {
        return getCurrentApplication().getDBApi();
    }
}
