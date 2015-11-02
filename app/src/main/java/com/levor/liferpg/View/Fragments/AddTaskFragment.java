package com.levor.liferpg.View.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.levor.liferpg.Adapters.TaskAddingAdapter;
import com.levor.liferpg.R;

import java.util.ArrayList;

public class AddTaskFragment extends DefaultFragment {
    private EditText newTaskTitleEditText;
    private ListView relatedSkillListView;
    private Button addSkillButton;
    private Button createTask;

    private ArrayList<String> relatedSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        newTaskTitleEditText = (EditText) view.findViewById(R.id.new_task_title_edit_text);

        relatedSkillListView = (ListView) view.findViewById(R.id.related_skills_to_add);
        relatedSkillListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                relatedSkills.remove(position);
                updateListView();
            }
        });
        updateListView();
        createTask = (Button) view.findViewById(R.id.create_task);
        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = newTaskTitleEditText.getText().toString();
                if (title.isEmpty()){
                    Toast.makeText(getActivity(), "Task title can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (relatedSkills.isEmpty()){
                    Toast.makeText(getActivity(), "Add at least one related skill", Toast.LENGTH_LONG).show();
                    return;
                }
                if (getController().getTaskByTitle(title) != null){
                    createIdenticalTaskRequestDialog(title);
                } else {
                    createNewTask(title);
                }
            }
        });
        addSkillButton = (Button) view.findViewById(R.id.add_related_skill);
        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item
                        , getController().getSkillsTitlesAndLevels().keySet().toArray(new String[getController().getSkillsTitlesAndLevels().size()]));

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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


        return view;
    }

    private void updateListView(){
        relatedSkillListView.setAdapter(new TaskAddingAdapter(getActivity(), relatedSkills));
    }

    private void createIdenticalTaskRequestDialog(final String title){
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
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
        getController().createNewTask(title, relatedSkills);
        Toast.makeText(getActivity(), "Task added", Toast.LENGTH_LONG).show();
        getCurrentActivity().showParentFragment(new TasksFragment());
    }
}
