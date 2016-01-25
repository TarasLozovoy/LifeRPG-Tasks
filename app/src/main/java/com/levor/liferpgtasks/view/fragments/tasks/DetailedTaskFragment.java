package com.levor.liferpgtasks.view.fragments.tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.PerformTaskAlertBuilder;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.skills.DetailedSkillFragment;

import java.text.DecimalFormat;
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
    private TextView noRelatedSkillsTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detailed_task, container, false);
        listView = (ListView) v;
        View header = LayoutInflater.from(getCurrentActivity()).inflate(R.layout.detailed_task_header, null);
        TextView taskTitleTV = (TextView) header.findViewById(R.id.task_title);
        TextView taskDifficultyTV = (TextView) header.findViewById(R.id.task_difficulty_text_view);
        TextView taskImportanceTV = (TextView) header.findViewById(R.id.task_importance_text_view);
        taskRepeatTV = (TextView) header.findViewById(R.id.task_repeat_times_text_view);
        taskDateTV = (TextView) header.findViewById(R.id.task_date_text_view);
        noRelatedSkillsTV = (TextView) header.findViewById(R.id.no_related_skills);
        TextView notificationTV = (TextView) header.findViewById(R.id.notification_text_view);


        UUID id = (UUID)getArguments().get(SELECTED_TASK_UUID_TAG);
        currentTask = getController().getTaskByID(id);
        taskTitleTV.setText(currentTask.getTitle());

        //setup task date
        setupTaskDate();

        //setup difficulty TextView
        int difficulty = currentTask.getDifficulty();
        String difficultyString = getString(R.string.difficulty) + " " +
                Arrays.asList(getResources().getStringArray(R.array.difficulties_array)).get(difficulty);
        taskDifficultyTV.setText(difficultyString);

        //setup importance TextView
        String importanceString = getString(R.string.importance) + " " +
                Arrays.asList(getResources().getStringArray(R.array.importance_array)).get(currentTask.getImportance());
        taskImportanceTV.setText(importanceString);

        //setup repeatability TextView
        setupRepeatability();

        //setup notification TextView
        notificationTV.setVisibility(currentTask.isNotify() ? View.VISIBLE : View.GONE);

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.task));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);

        listView.addHeaderView(header, null, false);
        setupListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG,
                        currentTask.getRelatedSkills().get(position - listView.getHeaderViewsCount()).getId());
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
        menu.findItem(R.id.perform_task).setVisible(!currentTask.isTaskDone())
                .setEnabled(!currentTask.isTaskDone());
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
                item.setVisible(false).setEnabled(false);
                undoTask();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Detailed Task Fragment");
    }

    private void setupTaskDate() {
        String dateString = getString(R.string.date) + " " +
                DateFormat.format(Task.getTimeFormatting(), currentTask.getDate()) + " - " +
                DateFormat.format(Task.getDateFormatting(), currentTask.getDate()) + " ";
        if (!currentTask.isTaskDone() && currentTask.getDate().before(new Date(System.currentTimeMillis()))){
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
            String repeatString = getString(R.string.repeat) + " " + repeat + " " +
                    getString(R.string.times);
            taskRepeatTV.setText(repeatString);
        }
    }

    private void setupListView(){
        ArrayList<String> skills = new ArrayList<>();
        for (Skill sk : currentTask.getRelatedSkills()) {
            if (sk == null) continue;
            DecimalFormat df = new DecimalFormat("#.##");
            skills.add(sk.getTitle() + " - " + sk.getLevel() + "(" + df.format(sk.getSublevel()) + ")");
        }
        noRelatedSkillsTV.setVisibility(skills.isEmpty() ? View.VISIBLE : View.GONE);
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, skills.toArray()));
    }

    private void performTask(){
        PerformTaskAlertBuilder alert = new PerformTaskAlertBuilder(getCurrentActivity(),
                currentTask,
                getView());
        AlertDialog alertDialog = alert.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setupListView();
            }
        });
        alertDialog.show();

        boolean isHeroLevelIncreased = getController().performTask(currentTask);
        if (currentTask.getRepeatability() == -1 || currentTask.getRepeatability() > 0){
            currentTask.increaseDateByNDays(1);
            getController().updateTaskNotification(currentTask);
            getController().updateTask(currentTask);
        }
        setupRepeatability();
        setupTaskDate();
        if (isHeroLevelIncreased) {
            Toast.makeText(getCurrentActivity(), getString(R.string.hero_level_increased,
                    getController().getHeroName()), Toast.LENGTH_SHORT).show();
        }
    }

    private void undoTask(){
        getController().undoTask(currentTask);
        setupListView();
        setupTaskDate();
        setupRepeatability();
        currentTask.increaseDateByNDays(-1);
        getController().updateTaskNotification(currentTask);
        getController().updateTask(currentTask);
    }
}
