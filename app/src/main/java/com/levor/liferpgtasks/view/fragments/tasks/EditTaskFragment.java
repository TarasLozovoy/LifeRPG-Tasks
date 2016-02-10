package com.levor.liferpgtasks.view.fragments.tasks;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
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
        if (getArguments() != null) {
            currentTask = getController().getTaskByTitle(getArguments().getString(CURRENT_TASK_TITLE_TAG));
            taskTitleEditText.setText(currentTask.getTitle());
            date = currentTask.getDate();
            dateMode = currentTask.getDateMode();
            repeatability = currentTask.getRepeatability();
            repeatMode = currentTask.getRepeatMode();
            repeatDaysOfWeek = currentTask.getRepeatDaysOfWeek();
            repeatIndex = currentTask.getRepeatIndex();
            difficulty = currentTask.getDifficulty();
            importance = currentTask.getImportance();
            notifyDelta = currentTask.getNotifyDelta();
            relatedSkills = new ArrayList<>();
            for (int i = 0; i < currentTask.getRelatedSkills().size(); i++) {
                Skill sk = currentTask.getRelatedSkills().get(i);
                if (sk != null) {
                    relatedSkills.add(sk.getTitle());
                }
            }
            updateUI();
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
                        repeatability = 1;
                        updateUI();
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
        alert.setTitle(getString(R.string.removing) + " " + currentTask.getTitle())
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
        currentTask.setTitle(title);
        currentTask.setDate(date);
        currentTask.setDateMode(dateMode);
        currentTask.setRepeatability(repeatability);
        currentTask.setRepeatMode(repeatMode);
        currentTask.setRepeatDaysOfWeek(repeatDaysOfWeek);
        currentTask.setRepeatIndex(repeatIndex);
        currentTask.setDifficulty(difficulty);
        currentTask.setImportance(importance);
        currentTask.setNotifyDelta(notifyDelta);
        List<Skill> skillsList = new ArrayList<>();
        for (int i = 0; i < relatedSkills.size(); i++) {
            Skill sk = getController().getSkillByTitle(relatedSkills.get(i));
            if (sk != null) {
                skillsList.add(sk);
            }
        }
        currentTask.setRelatedSkills(skillsList);
        getController().updateTask(currentTask);
        createNotification(currentTask);
        getCurrentActivity().showSoftKeyboard(false, getView());
        getCurrentActivity().showPreviousFragment();
    }

    @Override
    public boolean isDependableDataAvailable() {
        return getController().getTaskByTitle(getArguments().getString(CURRENT_TASK_TITLE_TAG)) != null;
    }
}
