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
    public final static String SELECTED_TASK_TITLE_TAG = "selected_task_title_tag";

    private String skillsFromFile;
    private String characteristicsFromFile;
    private String tasksFromFile;

    private Button openSkillsButton;
    private Button openCharacteristicsButton;
    private TextView tasksTextView;
    private ListView listView;
    private final LifeController lifeController = LifeController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        openSkillsButton = (Button) findViewById(R.id.openSkillsButton);
        openCharacteristicsButton = (Button) findViewById(R.id.openCharacteristicsButton);
        tasksTextView = (TextView) findViewById(R.id.tasks);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

    private void setupListView(){
        TasksAdapter adapter = new TasksAdapter(this, lifeController.getTasksTitlesAsList());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTaskTitle = lifeController.getTasksTitlesAsList().get(position);
                Intent intent = new Intent(TasksActivity.this, DetailedTaskActivity.class);
                intent.putExtra(SELECTED_TASK_TITLE_TAG, selectedTaskTitle);
                startActivity(intent);
            }
        });
    }

    private void showTasks(){
        Map<String, List<String>> tasks = lifeController.getTasksWithRelatedSkills();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> set : tasks.entrySet()){
            sb.append(set.getKey())
                    .append(".\nRelated skills: \n");
            for(String s : set.getValue()){
                sb.append('-')
                        .append(s)
                        .append('-')
                        .append(" ");
            }
            sb.append("\n");
        }
        tasksTextView.setText(sb);
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
