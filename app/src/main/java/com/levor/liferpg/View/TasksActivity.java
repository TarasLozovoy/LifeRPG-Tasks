package com.levor.liferpg.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;

import java.util.List;
import java.util.Map;

public class TasksActivity extends AppCompatActivity {
    private final static String LIFE_CONTROLLER_TAG = "life_controller_tag";

    private Button openSkillsButton, openCharacteristicsButton;
    private TextView tasksTextView;
    private final LifeController lifeController = LifeController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        openSkillsButton = (Button) findViewById(R.id.openSkillsButton);
        openCharacteristicsButton = (Button) findViewById(R.id.openCharacteristicsButton);
        tasksTextView = (TextView) findViewById(R.id.tasks);
        showTasks();
        registerButtonsListeners();
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
}
