package com.levor.liferpg.View.Obsolete;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;

import java.util.ArrayList;

public class DetailedTaskActivity extends AppCompatActivity {
    public final static String SELECTED_TASK_TITLE_TAG = "selected_task_title_tag";
    public final static int DETAILED_TASK_ACTIVITY_REQUEST_CODE = 1;
    private TextView taskTitle;
    private ListView listView;
    private Button removeTask;
    private Task currentTask;

    private final LifeController lifeController = LifeController.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task);
        taskTitle = (TextView) findViewById(R.id.task_title);
        listView = (ListView) findViewById(R.id.list_view);
        removeTask = (Button) findViewById(R.id.remove_task);

        String title = getIntent().getStringExtra(SELECTED_TASK_TITLE_TAG);
        taskTitle.setText(title);
        setTitle(title + " task details");

        currentTask = lifeController.getTaskByTitle(title);
        createAdapter();
        removeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DetailedTaskActivity.this);
                alert.setTitle("Removing " + currentTask.getTitle())
                        .setMessage("Are you really want to remove this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                lifeController.removeTask(currentTask);
                                finishActivity();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perform_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.perform_task) {
            StringBuilder sb = new StringBuilder();
            sb.append("Task successfully performed!\n")
                    .append("Skill(s) improved:\n");
            for(Skill sk: currentTask.getRelatedSkills()){
                sb.append(sk.getTitle())
                        .append(": ")
                        .append(sk.getLevel())
                        .append("(")
                        .append(sk.getSublevel())
                        .append(")");
                sk.increaseSublevel();
                sb.append(" -> ")
                        .append(sk.getLevel())
                        .append("(")
                        .append(sk.getSublevel())
                        .append(")")
                        .append("\n");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(currentTask.getTitle())
                    .setCancelable(false)
                    .setMessage(sb.toString())
                    .setPositiveButton("Nice!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            createAdapter();
                        }
                    })
                    .setNegativeButton("Undo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Task undone.");
                            for(Skill sk: currentTask.getRelatedSkills()){
                                sk.decreaseSublevel();
                                sb.append("\n").append(sk.getTitle()).append(" skill returned to previous state");
                            }
                            Toast.makeText(DetailedTaskActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void finishActivity(){
        setResult(RESULT_OK);
        finish();
    }

    private void createAdapter(){
        ArrayList<String> skills = new ArrayList<>();
        for (Skill sk : currentTask.getRelatedSkills()) {
            StringBuilder sb = new StringBuilder(sk.getTitle());
            sb.append(" - ")
                    .append(sk.getLevel())
                    .append("(")
                    .append(sk.getSublevel())
                    .append(")");
            skills.add(sb.toString());
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, skills.toArray()));
    }
}
