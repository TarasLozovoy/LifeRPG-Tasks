package com.levor.liferpg.View;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;

public class DetailedTaskActivity extends AppCompatActivity {
    private TextView taskTitle;
    private TextView relatedSkills;
    private Task currenttask;

    private final LifeController lifeController = LifeController.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task);
        taskTitle = (TextView) findViewById(R.id.task_title);
        relatedSkills = (TextView) findViewById(R.id.related_skills);

        String title = getIntent().getStringExtra(TasksActivity.SELECTED_TASK_TITLE_TAG);
        taskTitle.setText(title);

        currenttask = lifeController.getTaskByTitle(title);
        StringBuilder sb = new StringBuilder("Related skill(s):\n");
        for (Skill sk : currenttask.getRelatedSkills()){
            sb.append(sk.getTitle())
                    .append(" - ")
                    .append(sk.getLevel())
                    .append("(")
                    .append(sk.getSublevel())
                    .append(")");
        }
        relatedSkills.setText(sb.toString());
    }

}
