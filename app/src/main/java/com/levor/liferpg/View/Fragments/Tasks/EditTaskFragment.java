package com.levor.liferpg.View.Fragments.Tasks;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditTaskFragment extends AddTaskFragment {
    public final static String CURRENT_TASK_TAG = "current_task_tag";
    private Task currentTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        finishButton.setText(R.string.finish_editing);
        currentTask = getController().getTaskByTitle(getArguments().getString(CURRENT_TASK_TAG));
        newTaskTitleEditText.setText(currentTask.getTitle());
        for (Skill sk: currentTask.getRelatedSkills()){
            relatedSkills.add(sk.getTitle());
        }
        return v;
    }

    @Override
    protected void setFinishButtonOnClickListener() {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = newTaskTitleEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getActivity(), "Task title can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (relatedSkills.isEmpty()) {
                    Toast.makeText(getActivity(), "Add at least one related skill", Toast.LENGTH_LONG).show();
                    return;
                }
                if (getController().getTaskByTitle(title) != null && !title.equals(currentTask.getTitle())){
                    createIdenticalTaskRequestDialog(title);
                    return;
                }
                finishTask(title, "Task edit finished");
            }
        });
    }

    @Override
    protected void createIdenticalTaskRequestDialog(final String title) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Oops!")
                .setMessage("Another task with same title is already exists. Rewrite?")
                .setCancelable(true)
                .setNegativeButton("No, change current task title", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishTask(title, "Task edit finished");
                    }
                })
                .show();
    }

    @Override
    protected void finishTask(String title, String message) {
        currentTask.setTitle(title);
        List<Skill> skills = new ArrayList<>();
        for (String s: relatedSkills){
            skills.add(getController().getSkillByTitle(s));
        }
        currentTask.setRelatedSkills(skills);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }
}
