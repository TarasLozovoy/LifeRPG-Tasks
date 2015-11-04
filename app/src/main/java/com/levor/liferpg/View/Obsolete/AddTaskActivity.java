package com.levor.liferpg.View.Obsolete;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.levor.liferpg.Adapters.TaskAddingAdapter;
import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;

import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {
    private EditText newTaskTitleEditText;
    private ListView relatedSkillListView;
    private Button addSkillButton;

    private ArrayList<String> relatedSkills = new ArrayList<>();

    private final LifeController lifeController = LifeController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        newTaskTitleEditText = (EditText) findViewById(R.id.new_task_title_edit_text);

        relatedSkillListView = (ListView) findViewById(R.id.related_skills_to_add);
        relatedSkillListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                relatedSkills.remove(position);
                updateListView();
            }
        });
        updateListView();

        addSkillButton = (Button) findViewById(R.id.add_related_skill);
        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.select_dialog_item
                        , lifeController.getSkillsTitlesAndLevels().keySet().toArray(new String[lifeController.getSkillsTitlesAndLevels().size()]));

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddTaskActivity.this);
                dialog.setTitle("Choose skill to add");
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!relatedSkills.contains(adapter.getItem(which))) {
                            relatedSkills.add(adapter.getItem(which));
                            updateListView();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create_task) {
            String title = newTaskTitleEditText.getText().toString();
            if (title.isEmpty()){
                Toast.makeText(this, "Task title can't be empty", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (relatedSkills.isEmpty()){
                Toast.makeText(this, "Add at least one related skill", Toast.LENGTH_LONG).show();
                return true;
            }
            if (lifeController.getTaskByTitle(title) != null){
                createIdenticalTaskRequestDialog(title);
            } else {
                createNewTask(title);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateListView(){
        relatedSkillListView.setAdapter(new TaskAddingAdapter(this, relatedSkills));
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Task is not added", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    private void createIdenticalTaskRequestDialog(final String title){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Task with such title is already created!")
                .setMessage("Are you sure you want to rewrite old task with new one?")
                .setCancelable(true)
                .setNegativeButton("No, change new task title", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        createNewTask(title);
                    }
                })
                .show();

    }

    private void createNewTask(String title){
        setResult(RESULT_OK);
        lifeController.createNewTask(title, relatedSkills);
        Toast.makeText(this, "Task added", Toast.LENGTH_LONG).show();
        AddTaskActivity.this.finish();
    }
}
