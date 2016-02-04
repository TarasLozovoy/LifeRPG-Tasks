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
import com.levor.liferpgtasks.Utils.TimeUnitUtils;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.PerformTaskAlertBuilder;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.skills.DetailedSkillFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class DetailedTaskFragment extends DefaultFragment {
    public final static String SELECTED_TASK_UUID_TAG = "selected_task_uuid_tag";

    private ListView listView;
    private Task currentTask;
    private TextView taskRepeatTV;
    private TextView taskDateTV;
    private TextView notificationTV;
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
        notificationTV = (TextView) header.findViewById(R.id.notification_text_view);


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
        setupNotificationString();

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
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.date))
                .append(" ");
        if (currentTask.getDateMode() == Task.DateMode.TERMLESS){
            sb.append(getString(R.string.task_date_termless));
        } else {
            sb.append(DateFormat.format(Task.getDateFormatting(), currentTask.getDate()));
            if (currentTask.getDateMode() == Task.DateMode.SPECIFIC_TIME) {
                sb.append(" - ")
                        .append(DateFormat.format(Task.getTimeFormatting(), currentTask.getDate()))
                        .append(" ");
            }
            if (!currentTask.isTaskDone() && currentTask.getDate().before(new Date(System.currentTimeMillis()))) {
                sb.append(getString(R.string.overdue));
            }
        }
        taskDateTV.setText(sb.toString());
    }

    private void setupRepeatability() {
        int repeat = currentTask.getRepeatability();
        int repeatMode = currentTask.getRepeatMode();
        int repeatIndex = currentTask.getRepeatIndex();
        StringBuilder sb = new StringBuilder();
        if (repeat == 0) {
            sb.append(getString(R.string.task_finished));
        } else {
            sb.append(getString(R.string.repeat))
                    .append(": ");
            if (repeatMode == Task.RepeatMode.EVERY_NTH_DAY) {
                if (repeatIndex == 1) {
                    sb.append(getString(R.string.task_repeat_every_day));
                } else {
                    sb.append(getString(R.string.task_repeat_every_Nth_day, repeatIndex));
                }
                if (repeat > 0) {
                    sb.append("; ")
                            .append(getString(R.string.repeats))
                            .append(": ")
                            .append(repeat);
                }
            } else if (repeatMode == Task.RepeatMode.EVERY_NTH_MONTH) {
                if (repeatIndex == 1) {
                    sb.append(getString(R.string.task_repeat_every_month));
                } else {
                    sb.append(getString(R.string.task_repeat_every_Nth_month, repeatIndex));
                }
                if (repeat > 0) {
                    sb.append("; ")
                            .append(getString(R.string.repeats))
                            .append(": ")
                            .append(repeat);
                }
            } else if (repeatMode == Task.RepeatMode.EVERY_NTH_YEAR) {
                if (repeatIndex == 1) {
                    sb.append(getString(R.string.task_repeat_every_year));
                } else {
                    sb.append(getString(R.string.task_repeat_every_Nth_year, repeatIndex));
                }
                if (repeat > 0) {
                    sb.append("; ")
                            .append(getString(R.string.repeats))
                            .append(": ")
                            .append(repeat);
                }
            } else if (repeatMode == Task.RepeatMode.DO_NOT_REPEAT) {
                sb.append(getString(R.string.task_repeat_do_not_repeat));
            } else if (repeatMode == Task.RepeatMode.SIMPLE_REPEAT) {
                if (repeat > 0) {
                    sb.append(repeat);
                } else if (repeat == -1) {
                    sb.append(getString(R.string.infinite));
                }
            } else {    //weeks
                String[] days = getResources().getStringArray(R.array.days_of_week_short);
                for (int i = 0; i < days.length; i++) {
                    if (currentTask.getRepeatDaysOfWeek()[i]) {
                        sb.append(days[i])
                                .append(",");
                    }
                }
                if (Arrays.asList(currentTask.getRepeatDaysOfWeek()).contains(true)) {
                    sb.deleteCharAt(sb.length() - 1)
                            .append("; ");
                }

                if (repeatIndex == 1) {
                    sb.append(getString(R.string.task_repeat_every_week));
                } else {
                    sb.append(getString(R.string.task_repeat_every_Nth_week, repeatIndex));
                }
                if (repeat > 0) {
                    sb.append("; ")
                            .append(getString(R.string.repeats))
                            .append(": ")
                            .append(repeat);
                }
            }
        }
        taskRepeatTV.setText(sb.toString());
    }

    private void setupNotificationString(){
        if (currentTask.getNotifyDelta() >= 0) {
            notificationTV.setVisibility(View.VISIBLE);
            int dateMode = currentTask.getDateMode();
            long notifyDelta = currentTask.getNotifyDelta();
            StringBuilder sb = new StringBuilder(getString(R.string.notify));
            sb.append(" ");
            if (notifyDelta < 0 || dateMode == Task.DateMode.TERMLESS) {
                sb.append(getString(R.string.task_add_notification));
            } else {
                if (notifyDelta % TimeUnitUtils.WEEK == 0 && notifyDelta != 0) {
                    if (notifyDelta == TimeUnitUtils.WEEK) {
                        sb.append(getString(R.string.notify_1_week_before));
                    } else {
                        sb.append(getString(R.string.notify_N_weeks_before, notifyDelta / TimeUnitUtils.WEEK));
                    }
                } else if (notifyDelta % TimeUnitUtils.DAY == 0 && notifyDelta != 0) {
                    if (notifyDelta == TimeUnitUtils.DAY) {
                        sb.append(getString(R.string.notify_1_day_before));
                    } else {
                        sb.append(getString(R.string.notify_N_days_before, notifyDelta / TimeUnitUtils.DAY));
                    }
                } else if (notifyDelta % TimeUnitUtils.HOUR == 0 && notifyDelta != 0) {
                    if (notifyDelta == TimeUnitUtils.HOUR) {
                        sb.append(getString(R.string.notify_1_hour_before));
                    } else {
                        sb.append(getString(R.string.notify_N_hours_before, notifyDelta / TimeUnitUtils.HOUR));
                    }
                } else {
                    if (notifyDelta == TimeUnitUtils.MINUTE) {
                        sb.append(getString(R.string.notify_1_minute_before));
                    } else {
                        sb.append(getString(R.string.notify_N_minutes_before, notifyDelta / TimeUnitUtils.MINUTE));
                    }
                }
            }
            notificationTV.setText(sb.toString());
        } else {
            notificationTV.setVisibility(View.GONE);
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

    public Task getCurrentTask(){
        UUID id = (UUID)getArguments().get(SELECTED_TASK_UUID_TAG);
        return getController().getTaskByID(id);
    }
}
