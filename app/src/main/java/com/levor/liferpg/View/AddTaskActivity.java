package com.levor.liferpg.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;

import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {
    private EditText newTaskTitleEditText;
    private TextView relatedSkillToAdd;
    private Button addSkillButton;

    private ArrayList<String> relatedskills = new ArrayList<>();

    private final LifeController lifeController = LifeController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        newTaskTitleEditText = (EditText) findViewById(R.id.new_task_title_edit_text);
        relatedSkillToAdd = (TextView) findViewById(R.id.related_skills_to_add);
        addSkillButton = (Button) findViewById(R.id.add_related_skill);
        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AddTaskActivity.this, addSkillButton);
                popupMenu.getMenuInflater().inflate(R.menu.add_related_skill_popup_menu, popupMenu.getMenu());
                for (String sk : lifeController.getSkillsTitlesAndLevels().keySet()){
                    popupMenu.getMenu().add(sk);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        relatedskills.add(item.getTitle().toString());
                        relatedSkillToAdd.setText(relatedSkillToAdd.getText() + item.getTitle().toString() + " ");
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Task is not added", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}
