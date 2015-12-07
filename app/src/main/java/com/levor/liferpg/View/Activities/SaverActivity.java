package com.levor.liferpg.View.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.levor.liferpg.Controller.LifeController;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SaverActivity extends AppCompatActivity {
    final static private String APP_KEY = "wszp50bxwdv3yeh";
    final static private String APP_SECRET = "wr9p9g63xworvgu";
    private final String SKILLS_FILE_NAME = "skills_file_name.txt";
    private final String CHARACTERISTICS_FILE_NAME = "characteristics_file_name.txt";
    private final String TASKS_FILE_NAME = "tasks_file_name.txt";
    private final String HERO_FILE_NAME = "hero_file_name.txt";
    protected final String TAG = "com.levor.liferpg";
    protected final String APP = "com.levor.liferpg";

    private String skillsFromFile;
    private String characteristicsFromFile;
    private String tasksFromFile;
    private String heroFromFile;

    protected final LifeController lifeController = LifeController.getInstance(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readContentStringsFromFiles();
    }

    public void saveAppData() {
        writeContentStringsToFile();
    }

    public void readContentStringsFromFiles() {
        updateDataFromFile(CHARACTERISTICS_FILE_NAME);
        updateDataFromFile(SKILLS_FILE_NAME);
        updateDataFromFile(TASKS_FILE_NAME);
        updateDataFromFile(HERO_FILE_NAME);
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


    protected Fragment getCurrentFragment() {
        return null;
    }
}
