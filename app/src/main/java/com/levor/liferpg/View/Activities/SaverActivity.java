package com.levor.liferpg.View.Activities;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.RESTUtility;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AppKeyPair;
import com.levor.liferpg.Controller.LifeController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class SaverActivity extends AppCompatActivity {
    final static private String APP_KEY = "wszp50bxwdv3yeh";
    final static private String APP_SECRET = "wr9p9g63xworvgu";
    private final String SKILLS_FILE_NAME = "skills_file_name.txt";
    private final String CHARACTERISTICS_FILE_NAME = "characteristics_file_name.txt";
    private final String TASKS_FILE_NAME = "tasks_file_name.txt";
    private final String HERO_FILE_NAME = "hero_file_name.txt";
    protected final String TAG = "com.levor.liferpg";
    protected final String APP = "com.levor.liferpg";
    protected final String OAUTH2KEY = "com.levor.liferpg.dropboxAuthKey";
    protected final String SAVE_TO_DROPBOX_KEY = "com.levor.liferpg.saveToDropbox";

    private String skillsFromFile;
    private String characteristicsFromFile;
    private String tasksFromFile;
    private String heroFromFile;

    private DropboxAPI<AndroidAuthSession> mDBApi;
    private boolean saveToDropbox;
    private boolean authorisedToDropbox;
    private boolean attemptingToReconnect;
    protected final LifeController lifeController = LifeController.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        SharedPreferences prefs = this.getSharedPreferences(APP, Context.MODE_PRIVATE);
        saveToDropbox = prefs.getBoolean(SAVE_TO_DROPBOX_KEY, false);
        if (saveToDropbox) {
            String token = prefs.getString(OAUTH2KEY, null);
            if (token != null) {
                session.setOAuth2AccessToken(token);
                authorisedToDropbox = true;
            }
        }
        readContentStringsFromFiles();
    }

    protected void onResume() {
        super.onResume();

        if (saveToDropbox && !authorisedToDropbox && mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();
                authorisedToDropbox = true;
                attemptingToReconnect = false;
                String dropboxOAuth2AccessToken = mDBApi.getSession().getOAuth2AccessToken();
                SharedPreferences prefs = this.getSharedPreferences(APP, Context.MODE_PRIVATE);
                prefs.edit().putString(OAUTH2KEY, dropboxOAuth2AccessToken).apply();
                syncLocalAndDropBox();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = this.getSharedPreferences(APP, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(SAVE_TO_DROPBOX_KEY, saveToDropbox).apply();
    }

    public void startDropboxAuthorisation() {
        if (!authorisedToDropbox && !attemptingToReconnect) {
            attemptingToReconnect = true;
            setSaveToDropbox(true);
            AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(appKeys);
            mDBApi = new DropboxAPI<>(session);
            mDBApi.getSession().startOAuth2Authentication(SaverActivity.this);
        }
    }

    public void stopDropboxSync() {
        authorisedToDropbox = false;
        mDBApi = null;
    }

    public boolean isSaveToDropbox() {
        return saveToDropbox;
    }

    public void setSaveToDropbox(boolean saveToDropbox) {
        this.saveToDropbox = saveToDropbox;
    }

    public void saveAppData() {
        writeContentStringsToFile();
        if (authorisedToDropbox) {
            saveLocalFilesToDropbox();
        }
    }

    public void readContentStringsFromFiles() {
        updateDataFromFile(CHARACTERISTICS_FILE_NAME);
        updateDataFromFile(SKILLS_FILE_NAME);
        updateDataFromFile(TASKS_FILE_NAME);
        updateDataFromFile(HERO_FILE_NAME);
        if (authorisedToDropbox && isNetworkAvailable()) {
            syncLocalAndDropBox();
        }
        Log.e(TAG, "chars: " + characteristicsFromFile + "\nskiils: " + skillsFromFile + "\nTasks: " + tasksFromFile);
    }

    private void updateDataFromFile(String fileName) {
        switch (fileName) {
            case CHARACTERISTICS_FILE_NAME:
                characteristicsFromFile = getStringFromFile(CHARACTERISTICS_FILE_NAME);
                break;
            case SKILLS_FILE_NAME:
                skillsFromFile = getStringFromFile(SKILLS_FILE_NAME);
                break;
            case TASKS_FILE_NAME:
                tasksFromFile = getStringFromFile(TASKS_FILE_NAME);
                break;
            case HERO_FILE_NAME:
                heroFromFile = getStringFromFile(HERO_FILE_NAME);
                break;
        }
        lifeController.updateCurrentContentWithStrings(characteristicsFromFile, skillsFromFile, tasksFromFile, heroFromFile);
    }

    private String getStringFromFile(String fileName) {
        try {
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            fis.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected void writeContentStringsToFile() {
        writeStringToFile(lifeController.getCurrentCharacteristicsString(), CHARACTERISTICS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentSkillsString(), SKILLS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentTasksString(), TASKS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentHeroString(), HERO_FILE_NAME);
        Log.d(TAG, "content saved to filesystem");
    }

    private void writeStringToFile(String str, String fileName) {
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLocalFilesToDropbox() {
        saveFileToDropbox(CHARACTERISTICS_FILE_NAME);
        saveFileToDropbox(SKILLS_FILE_NAME);
        saveFileToDropbox(TASKS_FILE_NAME);
        saveFileToDropbox(HERO_FILE_NAME);
    }

    private void saveFileToDropbox(final String fileName) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String yourFilePath = SaverActivity.this.getFilesDir() + "/" + fileName;
                    File file = new File(yourFilePath);
                    FileInputStream inputStream = openFileInput(fileName);
                    DropboxAPI.Entry response = mDBApi.putFileOverwrite("/" + fileName, inputStream,
                            file.length(), null);
                    Log.i(TAG, "File is uploaded to DropBox. Rev: " + response.rev);
                } catch (DropboxUnlinkedException e) {
                    authorisedToDropbox = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(getCurrentFragment().getView(), "Dropbox authorozation is expired.\nPlease reconnect.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    startDropboxAuthorisation();
                } catch (IOException | DropboxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void syncLocalAndDropBox() {
        syncFileWithDropBox(CHARACTERISTICS_FILE_NAME);
        syncFileWithDropBox(SKILLS_FILE_NAME);
        syncFileWithDropBox(TASKS_FILE_NAME);
        syncFileWithDropBox(HERO_FILE_NAME);
    }

    private void syncFileWithDropBox(final String fileName) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String yourFilePath = SaverActivity.this.getFilesDir() + "/" + fileName;
                    File file = new File(yourFilePath);
                    Date lastModDateLocal = new Date(file.lastModified());
                    DropboxAPI.Entry existingEntry = mDBApi.metadata("/" + fileName, 1, null, false, null);
                    Date lastModDateLocalDropbox = RESTUtility.parseDate(existingEntry.modified);

                    if (lastModDateLocalDropbox.before(lastModDateLocal)) {
                        saveFileToDropbox(fileName);
                    } else {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/" + fileName, null, outputStream, null);
                        Log.i(TAG, "File is downloaded from DropBox. Rev: " + info.getMetadata().rev);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDataFromFile(fileName);
                                Snackbar.make(getCurrentFragment().getView(), "Synchronization with DropBox...", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (DropboxUnlinkedException e) {
                    authorisedToDropbox = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(getCurrentFragment().getView(), "Dropbox authorization is expired.\nPlease reconnect.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    startDropboxAuthorisation();
                } catch (IOException | DropboxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    protected Fragment getCurrentFragment() {
        return null;
    }
}
