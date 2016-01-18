package com.levor.liferpgtasks.view.fragments.tasks;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditTaskFragment extends AddTaskFragment {
    public final static String CURRENT_TASK_TITLE_TAG = "current_task_tag";
    private Task currentTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        currentTask = getController().getTaskByTitle(getArguments().getString(CURRENT_TASK_TITLE_TAG));
        if (savedInstanceState == null) {
            taskTitleEditText.setText(currentTask.getTitle());
            for (Skill sk : currentTask.getRelatedSkills()) {
                relatedSkills.add(sk.getTitle());
            }
            int repeat = currentTask.getRepeatability();
            if (repeat > 1) {
                repeatCheckbox.setChecked(true);
                taskRepeatEditText.setText(Integer.toString(repeat));
                repeatDetailedLayout.setVisibility(View.VISIBLE);
            } else if (repeat == -1){
                repeatCheckbox.setChecked(true);
                taskRepeatEditText.setText("");
                repeatDetailedLayout.setVisibility(View.VISIBLE);
            }
            if (repeat == 1) {
                repeatCheckbox.setChecked(false);
                taskRepeatEditText.setText("");
                repeatDetailedLayout.setVisibility(View.GONE);
            }
            difficultySpinner.setSelection(currentTask.getDifficulty());
            importanceSpinner.setSelection(currentTask.getImportance());
            setupDateTimeButtons(currentTask.getDate());
            notifyCheckbox.setChecked(currentTask.isNotify());
        }
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.edit_task_title));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        if (currentTask.isTaskDone()){
            showTaskAlreadyDoneDialog();
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_task:
                String title = taskTitleEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getCurrentActivity(),
                            getString(R.string.empty_title_task_error), Toast.LENGTH_LONG).show();
                    return true;
                }
                if (getController().getTaskByTitle(title) != null && !title.equals(currentTask.getTitle())){
                    createIdenticalTaskRequestDialog(title);
                    return true;
                }
                finishTask(title, getString(R.string.finish_edit_task_message));
                return true;

            case R.id.remove_task:
                removeTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Edit Task Fragment");
    }

    @Override
    protected void createIdenticalTaskRequestDialog(final String title) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(getString(R.string.oops))
                .setMessage(getString(R.string.task_duplicate_error))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.task_duplicate_negative_answer), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishTask(title, getString(R.string.finish_edit_task_message));
                    }
                })
                .show();
    }

    private void showTaskAlreadyDoneDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
        builder.setTitle(currentTask.getTitle())
                .setMessage(getString(R.string.task_already_done))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getCurrentActivity().showPreviousFragment();
                    }
                })
                .show();
    }
    private void removeTask(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.removing_task) + " " + currentTask.getTitle())
                .setMessage(getString(R.string.removing_task_description))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getController().removeTask(currentTask);
                        getCurrentActivity().showNthPreviousFragment(2);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void finishTask(String title, String message) {
        getCurrentActivity().showSoftKeyboard(false, getView());
        int repeat = getRepeatability();
        currentTask.setRepeatability(repeat);
        currentTask.setNotify(notifyCheckbox.isChecked());
        currentTask.setDifficulty(difficultySpinner.getSelectedItemPosition());
        currentTask.setImportance(importanceSpinner.getSelectedItemPosition());
        currentTask.setTitle(title);
        currentTask.setDate(date);
        List<Skill> skills = new ArrayList<>();
        for (String s: relatedSkills){
            skills.add(getController().getSkillByTitle(s));
        }
        currentTask.setRelatedSkills(skills);
        getController().updateTask(currentTask);
        createNotification(title);
        getCurrentActivity().showSoftKeyboard(false, getView());
        Snackbar.make(repeatDetailedLayout, message, Snackbar.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }
}
