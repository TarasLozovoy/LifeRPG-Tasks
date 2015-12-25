package com.levor.liferpgtasks.view.fragments.tasks;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.skills.DetailedSkillFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class DetailedTaskFragment extends DefaultFragment {
    public final static String SELECTED_TASK_UUID_TAG = "selected_task_uuid_tag";

    private ListView listView;
    private Task currentTask;
    private TextView taskRepeatTV;
    private TextView taskDateTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detailed_task, container, false);

        TextView taskTitleTV = (TextView) v.findViewById(R.id.task_title);
        TextView taskDifficultyTV = (TextView) v.findViewById(R.id.task_difficulty_text_view);
        TextView taskImportanceTV = (TextView) v.findViewById(R.id.task_importance_text_view);
        taskRepeatTV = (TextView) v.findViewById(R.id.task_repeat_times_text_view);
        taskDateTV = (TextView) v.findViewById(R.id.task_date_text_view);
        TextView notificationTV = (TextView) v.findViewById(R.id.notification_text_view);
        listView = (ListView) v.findViewById(R.id.list_view);

        UUID id = (UUID)getArguments().get(SELECTED_TASK_UUID_TAG);
        currentTask = getController().getTaskByID(id);
        taskTitleTV.setText(currentTask.getTitle());

        //setup task date
        setupTaskDate();

        //setup difficulty TextView
        int difficulty = currentTask.getDifficulty();
        String difficultyString = Arrays.asList(getResources()
                .getStringArray(R.array.difficulties_array)).get(difficulty);
        taskDifficultyTV.setText(getString(R.string.difficulty) +
                " " + difficultyString);

        //setup importance TextView
        String importanceString = Arrays.asList(getResources()
                .getStringArray(R.array.importance_array)).get(currentTask.getImportance());
        taskImportanceTV.setText(getString(R.string.importance) +
                " " + importanceString);

        //setup repeatability TextView
        setupRepeatability();

        //setup notification TextView
        notificationTV.setVisibility(currentTask.isNotify() ? View.VISIBLE : View.GONE);

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Task");
        getCurrentActivity().showActionBarHomeButtonAsBack(true);

        setupListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG, currentTask.getRelatedSkills().get(position).getId());
                DefaultFragment f = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_detailed_task, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.perform_task).setEnabled(!currentTask.isTaskDone());
        menu.findItem(R.id.undo_task).setVisible(currentTask.isUndonable())
                .setEnabled(currentTask.isUndonable());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_task:
                Bundle b = new Bundle();
                b.putSerializable(EditTaskFragment.CURRENT_TASK_TITLE_TAG, currentTask.getTitle());
                DefaultFragment f = new EditTaskFragment();
                getCurrentActivity().showChildFragment(f, b);
                return true;
            case R.id.perform_task:
                performTask();
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.undo_task:
                getController().undoTask(currentTask);
                item.setVisible(false).setEnabled(false);
                setupListView();
                return true;
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupTaskDate() {
        String dateString = getString(R.string.date) + " " +
                DateFormat.format(Task.getTimeFormatting(), currentTask.getDate()) + " - " +
                DateFormat.format(Task.getDateFormatting(), currentTask.getDate()) + " ";
        if (currentTask.getDate().before(new Date(System.currentTimeMillis()))){
            dateString = dateString + getString(R.string.overdue);
        }
        taskDateTV.setText(dateString);
    }

    private void setupRepeatability() {
        int repeat = currentTask.getRepeatability();
        if (repeat == 0) {
            taskRepeatTV.setText(getString(R.string.task_finished));
        } else if (repeat < 0) {
            taskRepeatTV.setText(getString(R.string.infinite_number_of));
        } else {
            taskRepeatTV.setText(getString(R.string.repeat) + " " +
                    repeat + " " +
                    getString(R.string.times));
        }
    }

    private void setupListView(){
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
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, skills.toArray()));
    }

    private void performTask(){
        boolean isHeroLevelIncreased = getController().performTask(currentTask, true);
        if (currentTask.getRepeatability() == -1 || currentTask.getRepeatability() > 0){
            currentTask.increaseDateByOneDay();
            getController().updateTaskNotification(currentTask);
            getController().updateTask(currentTask);
        }
        setupRepeatability();
        setupTaskDate();
        if (isHeroLevelIncreased) {
            Snackbar.make(getView(), "Congratulations!\n" + getController().getHeroName()
                    + "'s level increased!", Snackbar.LENGTH_LONG)
                    .setAction("Go to Hero page", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCurrentActivity().switchToRootFragment(MainActivity.MAIN_FRAGMENT_ID);
                        }
                    })
                    .show();
        }
        double xp = getController().getHero().getBaseXP() * currentTask.getMultiplier();

        AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
        builder.setTitle(currentTask.getTitle())
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.task_performed) + "\n" + getResources().getString(R.string.XP_gained, xp))
                .setNeutralButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setupListView();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.share), null);
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new ShareClickListener(currentTask.getTitle(), alert));
            }
        });
        alert.show();
    }

    private class ShareClickListener implements View.OnClickListener{
        private String taskTitle;
        private AlertDialog dialog;

        public ShareClickListener(String task, AlertDialog dialog){
            this.taskTitle = task;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v){
            if (!getController().isInternetConnectionActive()) {
                Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                return;
            }
            boolean heroLevelIncreased = getController().performTask(getController().getTaskByTitle(taskTitle), false);
            if (heroLevelIncreased){
                Snackbar.make(getView(), "Congratulations!\n" + getController().getHeroName()
                        + "'s level increased!", Snackbar.LENGTH_LONG)
                        .setAction("Go to Hero page", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getCurrentActivity().switchToRootFragment(MainActivity.MAIN_FRAGMENT_ID);
                            }
                        })
                        .show();
            }
            setupListView();

            ShareDialog shareDialog = new ShareDialog(getActivity());
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(taskTitle + " " + getActivity().getResources().getString(R.string.done))
                        .setContentDescription(
                                "I have just finished task " + taskTitle + "!")
                        .setContentUrl(Uri.parse(getActivity().getResources().getString(R.string.facebook_app_link)))
                        .build();

                shareDialog.show(linkContent);
            }
            dialog.dismiss();
        }
    }
}
