package com.levor.liferpg.View;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpg.Adapters.TasksAdapter;
import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;

import java.util.ArrayList;

public class DetailedSkillActivity extends AppCompatActivity {
    private TextView skillTitleTV;
    private TextView keyCharTV;
    private TextView levelValue;
    private TextView sublevelValue;
    private TextView toNextLevel;
    private ListView listView;

    private final LifeController lifeController = LifeController.getInstance();
    private Skill currentSkill;
    private ArrayList<String> currentTasks;
    private TasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_skill);

        skillTitleTV = (TextView) findViewById(R.id.skill_title);
        keyCharTV = (TextView) findViewById(R.id.key_char);
        levelValue = (TextView) findViewById(R.id.level_value);
        sublevelValue = (TextView) findViewById(R.id.sublevel_value);
        toNextLevel = (TextView) findViewById(R.id.to_next_level_value);
        listView = (ListView) findViewById(R.id.related_tasks);
        currentSkill = lifeController.getSkillByTitle(getIntent().getStringExtra(SkillsActivity.SELECTED_SKILL_TITLE_TAG));
        setTitle(currentSkill.getTitle() + " skill details");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTaskTitle = currentTasks.get(position);
                Intent intent = new Intent(DetailedSkillActivity.this, DetailedTaskActivity.class);
                intent.putExtra(DetailedTaskActivity.SELECTED_TASK_TITLE_TAG, selectedTaskTitle);
                startActivityForResult(intent, DetailedTaskActivity.DETAILED_TASK_ACTIVITY_REQUEST_CODE);
            }
        });
        createAdapter();
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                currentSkill = lifeController.getSkillByTitle(currentSkill.getTitle());
                updateSkillDetails();
            }
        });
        updateSkillDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_skill, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DetailedTaskActivity.DETAILED_TASK_ACTIVITY_REQUEST_CODE :
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void createAdapter(){
        ArrayList<Task> tasks = lifeController.getTasksBySkill(currentSkill);
        ArrayList<String> titles = new ArrayList<>();
        for (Task t: tasks){
            titles.add(t.getTitle());
        }
        currentTasks = titles;
        adapter = new TasksAdapter(this, titles);
        listView.setAdapter(adapter);
    }

    private void updateSkillDetails(){
        skillTitleTV.setText(currentSkill.getTitle());
        keyCharTV.setText(currentSkill.getKeyCharacteristic().getTitle());
        levelValue.setText(" "+currentSkill.getLevel());
        sublevelValue.setText(" "+currentSkill.getSublevel());
        toNextLevel.setText(" "+(currentSkill.getLevel() - currentSkill.getSublevel()));
    }
}
