package com.levor.liferpg.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    private Button finishActivity;

    private ArrayList<String> relatedskills = new ArrayList<>();

    private final LifeController lifeController = LifeController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        newTaskTitleEditText = (EditText) findViewById(R.id.new_task_title_edit_text);
        relatedSkillToAdd = (TextView) findViewById(R.id.related_skills_to_add);
        finishActivity = (Button) findViewById(R.id.finish_activity);
        finishActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                lifeController.createNewTask(newTaskTitleEditText.getText().toString(), relatedskills);
                AddTaskActivity.this.finish();
            }
        });
        addSkillButton = (Button) findViewById(R.id.add_related_skill);
        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.select_dialog_singlechoice
                        , lifeController.getSkillsTitlesAndLevels().keySet().toArray(new String[lifeController.getSkillsTitlesAndLevels().size()]));

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddTaskActivity.this);
                dialog.setTitle("Choose skill to add");
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        relatedskills.add(adapter.getItem(which));
                        relatedSkillToAdd.setText(relatedSkillToAdd.getText() + adapter.getItem(which) + " ");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Task is not added", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}
