package com.levor.liferpg.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpg.Adapters.TasksAdapter;
import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class TasksActivity extends AppCompatActivity {
    private final String SKILLS_FILE_NAME = "skills_file_name.txt";
    private final String CHARACTERISTICS_FILE_NAME = "characteristics_file_name.txt";
    private final String TASKS_FILE_NAME = "tasks_file_name.txt";
    private final String TAG = "com.levor.liferpg";
    public final static int ADD_TASK_ACTIVITY_REQUEST_CODE = 0;

    private String skillsFromFile;
    private String characteristicsFromFile;
    private String tasksFromFile;

    private Button openSkillsButton;
    private Button openCharacteristicsButton;
    private ListView listView;
    private final LifeController lifeController = LifeController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        openSkillsButton = (Button) findViewById(R.id.openSkillsButton);
        openCharacteristicsButton = (Button) findViewById(R.id.openCharacteristicsButton);
        listView = (ListView) findViewById(R.id.listViewTasks);

        readContentStringsFromFiles();
        setupListView();
        registerButtonsListeners();
    }

    @Override
    protected void onPause() {
        writeContentStringsToFile();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_new_task) {
            startActivityForResult(new Intent(TasksActivity.this, AddTaskActivity.class), ADD_TASK_ACTIVITY_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerButtonsListeners(){
        openCharacteristicsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TasksActivity.this, CharacteristicActivity.class);
                startActivity(intent);
            }
        });

        openSkillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TasksActivity.this, SkillsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_TASK_ACTIVITY_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    recreateAdapter();
                }
                break;
            default:
                //do nothing
        }
    }

    private void setupListView(){
        TasksAdapter adapter = new TasksAdapter(this, lifeController.getTasksTitlesAsList());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTaskTitle = lifeController.getTasksTitlesAsList().get(position);
                Intent intent = new Intent(TasksActivity.this, DetailedTaskActivity.class);
                intent.putExtra(DetailedTaskActivity.SELECTED_TASK_TITLE_TAG, selectedTaskTitle);
                startActivity(intent);
            }
        });
    }

    private void readContentStringsFromFiles(){
        characteristicsFromFile = getStringFromFile(CHARACTERISTICS_FILE_NAME);
        skillsFromFile = getStringFromFile(SKILLS_FILE_NAME);
        tasksFromFile = getStringFromFile(TASKS_FILE_NAME);
        Log.e(TAG, "chars: " + characteristicsFromFile + "\nskiils: " + skillsFromFile + "\nTasks: " + tasksFromFile);
        lifeController.updateCurrentContentWithStrings(characteristicsFromFile, skillsFromFile, tasksFromFile);
        recreateAdapter();
    }

    private String getStringFromFile(String fileName){
        try{
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            fis.close();
            return sb.toString();
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return "";
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    private void writeContentStringsToFile(){
        writeStringToFile(lifeController.getCurrentCharacteristicsString(), CHARACTERISTICS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentSkillsString(), SKILLS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentTasksString(), TASKS_FILE_NAME);
        Log.d(TAG, "content saved to filesystem");
    }

    private void writeStringToFile(String str, String fileName){
        try{
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void recreateAdapter(){
        listView.setAdapter(new TasksAdapter(this, lifeController.getTasksTitlesAsList()));
    }
}
