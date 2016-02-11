package com.levor.liferpgtasks.view.fragments.tasks;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.Utils.TimeUnitUtils;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.model.Task.RepeatMode;
import com.levor.liferpgtasks.view.fragments.DataDependantFrament;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.levor.liferpgtasks.model.Task.DateMode;

public class AddTaskFragment extends DataDependantFrament {
    public static final String RECEIVED_SKILL_TITLE_TAG = "received_skill_tag";

    public static final String REPEAT_MODE_TAG = "repeat_mode_tag";
    public static final String REPEAT_TAG = "repeat_tag";
    public static final String TASK_TITLE_TAG = "task_title_tag";
    private final String RELATED_SKILLS_TAG = "related_skills_tag";
//    private final String DIFFICULTY_TAG = "difficulty_tag";
//    private final String IMPORTANCE_TAG = "importance_tag";
    private final String DATE_TAG = "date_tag";
//    private final String NOTIFY_TAG = "notify_tag";
//    private final String REPEAT_CHECKBOX_TAG = "repeat_checkbox_tag";

    protected EditText taskTitleEditText;
    protected TextView dateTextView;
    protected View dateView;
    protected TextView notifyTextView;
    protected View notifyView;
    protected ImageView notifyImageView;
    protected TextView repeatTextView;
    protected View repeatView;
    protected ImageView repeatImageView;
    protected TextView difficultyTextView;
    protected View difficultyView;
    protected TextView importanceTextView;
    protected View importanceView;
    protected TextView relatedSkillsTextView;
    protected View relatedSkillsView;

    protected Date date;
    protected int dateMode = DateMode.TERMLESS;
    protected int repeatability = 1;
    protected int repeatMode;
    protected Boolean[] repeatDaysOfWeek;
    protected int repeatIndex = 1;      //repeat every N days, repeatIndex == N
    protected long notifyDelta = -1;         // <0 - do not notify, >0 notify at (date - delta) time
    protected int difficulty = Task.LOW;
    protected int importance = Task.LOW;
    protected ArrayList<String> relatedSkills = new ArrayList<>();

    private int notifyEditTextMaxValue = 600;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_add_task, container, false);
        taskTitleEditText = (EditText) v.findViewById(R.id.task_title_edit_text);
        dateTextView = (TextView) v.findViewById(R.id.date_time_text_view);
        dateView = v.findViewById(R.id.date_time_layout);
        repeatTextView = (TextView) v.findViewById(R.id.repeat_text_view);
        repeatView = v.findViewById(R.id.repeat_layout);
        repeatImageView = (ImageView) v.findViewById(R.id.repeat_image_view);
        notifyTextView = (TextView) v.findViewById(R.id.notification_text_view);
        notifyView = v.findViewById(R.id.notification_layout);
        notifyImageView = (ImageView) v.findViewById(R.id.notify_image_view);
        difficultyTextView = (TextView) v.findViewById(R.id.difficulty_text_view);
        difficultyView = v.findViewById(R.id.difficulty_layout);
        importanceTextView = (TextView) v.findViewById(R.id.importance_text_view);
        importanceView = v.findViewById(R.id.importance_layout);
        relatedSkillsTextView = (TextView) v.findViewById(R.id.related_skills_text_view);
        relatedSkillsView = v.findViewById(R.id.related_skills_layout);

        dateTextView.setText(getString(R.string.task_date_termless));
        repeatTextView.setText(getString(R.string.task_repeat_do_not_repeat));
        notifyTextView.setText(getString(R.string.task_add_notification));
        String difficultyString = getString(R.string.difficulty) + " " + getResources().getStringArray(R.array.difficulties_array)[0];
        difficultyTextView.setText(difficultyString);
        String importanceString = getString(R.string.importance) + " " + getResources().getStringArray(R.array.importance_array)[0];
        importanceTextView.setText(importanceString);
        relatedSkillsTextView.setText(R.string.add_skill_to_task);
        registerListeners();
        if (savedInstanceState != null) {
            taskTitleEditText.setText(savedInstanceState.getString(TASK_TITLE_TAG));
            relatedSkills = savedInstanceState.getStringArrayList(RELATED_SKILLS_TAG);
            date = new Date (savedInstanceState.getLong(DATE_TAG));
        } else {
            date = new Date();
        }
        if (getArguments() != null){
            repeatMode = getArguments().getInt(REPEAT_MODE_TAG, RepeatMode.SIMPLE_REPEAT);
            repeatability = getArguments().getInt(REPEAT_TAG, 1);
            dateMode = repeatMode == RepeatMode.EVERY_NTH_DAY ? DateMode.WHOLE_DAY : dateMode;

            String skillTitle;
            if ((skillTitle = getArguments().getString(RECEIVED_SKILL_TITLE_TAG)) != null){
                relatedSkills.add(skillTitle);
            }
        }

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.add_new_task));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Add Task Fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.finish_creating_task:
                String title = taskTitleEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getCurrentActivity(),
                            getString(R.string.empty_title_task_error), Toast.LENGTH_LONG).show();
                    return true;
                }
                if (getController().getTaskByTitle(title) != null) {
                    createIdenticalTaskRequestDialog(title);
                    return true;
                } else {
                    finishTask(title, getString(R.string.new_task_added) + " " + title);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(TASK_TITLE_TAG, taskTitleEditText != null ? taskTitleEditText.getText().toString() : "");
        outState.putSerializable(RELATED_SKILLS_TAG, relatedSkills);
        outState.putSerializable(DATE_TAG, date);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateDateView();
        updateNotifyView();
        updateRepeatView();
        updateDifficultyView();
        updateImportanceView();
        updateRelatedSkillsView();
    }

    protected void createIdenticalTaskRequestDialog(final String title){
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
                        finishTask(title, getString(R.string.new_task_added) + " " + title);
                    }
                })
                .show();

    }

    protected void finishTask(String title, String message){
        Task task = new Task(title);
        task.setDate(date);
        task.setDateMode(dateMode);
        task.setRepeatability(repeatability);
        task.setRepeatMode(repeatMode);
        task.setRepeatDaysOfWeek(repeatDaysOfWeek);
        task.setRepeatIndex(repeatIndex);
        task.setDifficulty(difficulty);
        task.setImportance(importance);
        task.setNotifyDelta(notifyDelta);
        List<Skill> skillsList = new ArrayList<>();
        for (int i = 0; i < relatedSkills.size(); i++) {
            Skill sk = getController().getSkillByTitle(relatedSkills.get(i));
            if (sk != null) {
                skillsList.add(sk);
            }
        }
        task.setRelatedSkills(skillsList);
        getController().createNewTask(task);

        getController().getGATracker().send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.GA_action))
                .setAction(getString(R.string.GA_add_new_task))
                .build());

        createNotification(task);
        getCurrentActivity().showSoftKeyboard(false, getView());
        Toast.makeText(getCurrentActivity(), message, Toast.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }

    private void registerListeners() {
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] dateVariants = getResources().getStringArray(R.array.date_pick_array);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.select_dialog_item, dateVariants);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dateMode = DateMode.TERMLESS;
                                break;
                            case 1:
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.HOUR_OF_DAY, 23);
                                cal.set(Calendar.MINUTE, 59);
                                cal.set(Calendar.SECOND, 59);
                                date = cal.getTime();
                                dateMode = DateMode.WHOLE_DAY;
                                break;
                            case 2:
                                Calendar c = Calendar.getInstance();
                                c.add(Calendar.DAY_OF_MONTH, 1);
                                c.set(Calendar.HOUR_OF_DAY, 23);
                                c.set(Calendar.MINUTE, 59);
                                c.set(Calendar.SECOND, 59);
                                date = c.getTime();
                                dateMode = DateMode.WHOLE_DAY;
                                break;
                            case 3:
                                showDatePickerDialog();
                                break;
                        }
                        updateDateView();
                    }
                }).show();
            }
        });

        repeatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] repeatVariants = getResources().getStringArray(R.array.repeat_pick_array);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.select_dialog_item, repeatVariants);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                repeatability = 1;
                                repeatMode = RepeatMode.DO_NOT_REPEAT;
                                break;
                            case 1:
                                repeatability = -1;
                                repeatMode = RepeatMode.EVERY_NTH_DAY;
                                repeatIndex = 1;
                                break;
                            case 2:
                                repeatability = -1;
                                repeatMode = RepeatMode.DAYS_OF_NTH_WEEK;
                                repeatIndex = 1;
                                Calendar cal = Calendar.getInstance();
                                if (date != null) cal.setTime(date);
                                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // for sunday - 0, monday - 1...
                                repeatDaysOfWeek = new Boolean[7];
                                for (int i = 0; i < repeatDaysOfWeek.length; i++) {
                                    repeatDaysOfWeek[i] = false;
                                }
                                repeatDaysOfWeek[dayOfWeek] = true;
                                break;
                            case 3:
                                repeatability = -1;
                                repeatMode = RepeatMode.EVERY_NTH_MONTH;
                                repeatIndex = 1;
                                break;
                            case 4:
                                repeatability = -1;
                                repeatMode = RepeatMode.EVERY_NTH_YEAR;
                                repeatIndex = 1;
                                break;
                            case 5:
                                showCustomRepeatDialog();
                                break;
                        }
                        updateRepeatView();
                    }
                }).show();
            }
        });

        notifyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] notifyVariants = getResources().getStringArray(R.array.notify_dialog_items);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.select_dialog_item, notifyVariants);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //do not notify
                                notifyDelta = -1;
                                break;
                            case 1: //1 minute before
                                notifyDelta = TimeUnitUtils.MINUTE;
                                break;
                            case 2: //10 minute before
                                notifyDelta = 10 * TimeUnitUtils.MINUTE;
                                break;
                            case 3: //60 minute before
                                notifyDelta = 60 * TimeUnitUtils.MINUTE;
                                break;
                            case 4: //custom
                                showCustomNotifyDialog();
                                break;
                        }
                        updateNotifyView();
                    }
                }).show();
            }
        });

        difficultyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] notifyVariants = getResources().getStringArray(R.array.difficulties_array);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.select_dialog_item, notifyVariants);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        difficulty = which;
                        updateDifficultyView();
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        importanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] notifyVariants = getResources().getStringArray(R.array.importance_array);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.select_dialog_item, notifyVariants);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        importance = which;
                        updateImportanceView();
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        relatedSkillsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelatedSkillSelectionDialog dialog = new RelatedSkillSelectionDialog();
                dialog.show(getCurrentActivity().getSupportFragmentManager(), "SkillSelection");
            }
        });
    }

    private void updateDateView() {
        StringBuilder sb = new StringBuilder();
        int startRepeatMode = repeatMode;
        if (dateMode == DateMode.TERMLESS) {
            sb.append(getString(R.string.task_date_termless));
            if (repeatMode == RepeatMode.EVERY_NTH_DAY || repeatMode == RepeatMode.EVERY_NTH_MONTH ||
                    repeatMode == RepeatMode.EVERY_NTH_YEAR || repeatMode == RepeatMode.DAYS_OF_NTH_WEEK){
                repeatMode = repeatability > 0 ? RepeatMode.SIMPLE_REPEAT : RepeatMode.DO_NOT_REPEAT;
            }
            if (notifyView.isEnabled()){
                notifyDelta = -1;
                notifyView.setEnabled(false);
                notifyTextView.setEnabled(false);
                notifyImageView.setAlpha(0.4f);
                updateNotifyView();
            }
        } else {
            sb.append(DateFormat.format(Task.getDateFormatting(), date));
            if (dateMode == DateMode.SPECIFIC_TIME) {
                sb.append(" ");
                sb.append(DateFormat.format(Task.getTimeFormatting(), date));
            }
            if (repeatMode == RepeatMode.SIMPLE_REPEAT) {
                repeatMode = RepeatMode.EVERY_NTH_DAY;
                repeatIndex = 1;
            } else if (repeatMode == RepeatMode.DAYS_OF_NTH_WEEK) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int currentDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
                if (!repeatDaysOfWeek[currentDay]) {
                    repeatDaysOfWeek = new Boolean[7];
                    for (int i = 0; i < repeatDaysOfWeek.length; i++) {
                        repeatDaysOfWeek[i] = false;
                    }
                    repeatDaysOfWeek[currentDay] = true;
                    updateRepeatView();
                }
            }
            if (!notifyView.isEnabled()) {
                notifyView.setEnabled(true);
                notifyTextView.setEnabled(true);
                notifyImageView.setAlpha(1f);
            }
        }
        dateTextView.setText(sb.toString());
        if (startRepeatMode != repeatMode) updateRepeatView();
    }

    private void updateRepeatView(){
        int startDateMode = dateMode;
        StringBuilder sb = new StringBuilder();
        switch (repeatMode) {
            case RepeatMode.EVERY_NTH_DAY:
                if (repeatIndex == 1) {
                    sb.append(getString(R.string.task_repeat_every_day));
                } else {
                    sb.append(getString(R.string.task_repeat_every_Nth_day, repeatIndex));
                }
                if (repeatability > 0) {
                    sb.append("; ")
                            .append(getString(R.string.repeats))
                            .append(": ")
                            .append(repeatability);
                }
                break;
            case RepeatMode.EVERY_NTH_MONTH:
                if (repeatIndex == 1) {
                    sb.append(getString(R.string.task_repeat_every_month));
                } else {
                    sb.append(getString(R.string.task_repeat_every_Nth_month, repeatIndex));
                }
                if (repeatability > 0) {
                    sb.append("; ")
                            .append(getString(R.string.repeats))
                            .append(": ")
                            .append(repeatability);
                }
                break;
            case RepeatMode.EVERY_NTH_YEAR:
                if (repeatIndex == 1) {
                    sb.append(getString(R.string.task_repeat_every_year));
                } else {
                    sb.append(getString(R.string.task_repeat_every_Nth_year, repeatIndex));
                }
                if (repeatability > 0) {
                    sb.append("; ")
                            .append(getString(R.string.repeats))
                            .append(": ")
                            .append(repeatability);
                }
                break;
            case RepeatMode.DAYS_OF_NTH_WEEK:
                String[] days = getResources().getStringArray(R.array.days_of_week_short);
                for (int i = 0; i < days.length; i++) {
                    if (repeatDaysOfWeek[i]) {
                        sb.append(days[i])
                                .append(",");
                    }
                }
                if (Arrays.asList(repeatDaysOfWeek).contains(true)) {
                    sb.deleteCharAt(sb.length() - 1)
                            .append("; ");
                }

                if (repeatIndex == 1) {
                    sb.append(getString(R.string.task_repeat_every_week));
                } else {
                    sb.append(getString(R.string.task_repeat_every_Nth_week, repeatIndex));
                }
                if (repeatability > 0) {
                    sb.append("; ")
                            .append(getString(R.string.repeats))
                            .append(": ")
                            .append(repeatability);
                }

                Calendar cal = Calendar.getInstance();
                if (dateMode == DateMode.TERMLESS) {
                    dateMode = DateMode.WHOLE_DAY;
                } else {
                    startDateMode = DateMode.TERMLESS;
                    cal.setTime(date);
                }
                while (true) {
                    if (repeatDaysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1]) break;
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                }
                date = cal.getTime();

                break;
            case RepeatMode.DO_NOT_REPEAT:
                sb.append(getString(R.string.task_repeat_do_not_repeat));
                break;
            case RepeatMode.SIMPLE_REPEAT:
                sb.append(getString(R.string.repeats))
                        .append(": ");
                if (repeatability > 0) {
                    sb.append(repeatability);
                } else if (repeatability < 0) {
                    sb.append(getString(R.string.infinite));
                }
                if (dateMode != DateMode.TERMLESS){
                    dateMode = DateMode.TERMLESS;
                }
                break;
        }
        if (repeatMode <= 2 && dateMode == DateMode.TERMLESS) {
            dateMode = DateMode.WHOLE_DAY;
            date = new Date();
        }

        //setting repeat image view
        if (repeatMode == RepeatMode.SIMPLE_REPEAT){
            repeatImageView.setImageResource(R.drawable.ic_replay_black_24dp);
            repeatImageView.setAlpha(1f);
        } else if (repeatMode == RepeatMode.DO_NOT_REPEAT){
            repeatImageView.setImageResource(R.drawable.ic_replay_black_24dp);
            repeatImageView.setAlpha(0.4f);
        } else {
            repeatImageView.setImageResource(R.drawable.infinity);
            repeatImageView.setAlpha(1f);
        }
        //

        repeatTextView.setText(sb.toString());
        if (startDateMode != dateMode) updateDateView();
    }

    private void updateNotifyView(){
        StringBuilder sb = new StringBuilder();
        if (notifyDelta < 0 || dateMode == DateMode.TERMLESS) {
            sb.append(getString(R.string.task_add_notification));
        } else {
            if (notifyDelta % TimeUnitUtils.WEEK == 0 && notifyDelta != 0){
                if (notifyDelta == TimeUnitUtils.WEEK){
                    sb.append(getString(R.string.notify_1_week_before));
                } else {
                    sb.append(getString(R.string.notify_N_weeks_before, notifyDelta/TimeUnitUtils.WEEK));
                }
            } else if (notifyDelta % TimeUnitUtils.DAY == 0 && notifyDelta != 0){
                if (notifyDelta == TimeUnitUtils.DAY){
                    sb.append(getString(R.string.notify_1_day_before));
                } else {
                    sb.append(getString(R.string.notify_N_days_before, notifyDelta/TimeUnitUtils.DAY));
                }
            } else if (notifyDelta % TimeUnitUtils.HOUR == 0 && notifyDelta != 0){
                if (notifyDelta == TimeUnitUtils.HOUR){
                    sb.append(getString(R.string.notify_1_hour_before));
                } else {
                    sb.append(getString(R.string.notify_N_hours_before, notifyDelta/TimeUnitUtils.HOUR));
                }
            } else {
                if (notifyDelta == TimeUnitUtils.MINUTE){
                    sb.append(getString(R.string.notify_1_minute_before));
                } else {
                    sb.append(getString(R.string.notify_N_minutes_before, notifyDelta/TimeUnitUtils.MINUTE));
                }
            }
        }
        notifyTextView.setText(sb.toString());
    }

    protected void updateDifficultyView() {
        String difficultyString = getString(R.string.difficulty) + " "
                + getResources().getStringArray(R.array.difficulties_array)[difficulty];
        difficultyTextView.setText(difficultyString);
    }

    private void updateImportanceView() {
        String importanceString = getString(R.string.importance) + " " +
                getResources().getStringArray(R.array.importance_array)[importance];
        importanceTextView.setText(importanceString);
    }

    protected void updateRelatedSkillsView(){
        StringBuilder sb = new StringBuilder();
        if (relatedSkills.isEmpty()){
            sb.append(getString(R.string.add_skill_to_task));
        } else {
            sb.append(getString(R.string.related_skills_list));
            sb.append("\n");
            for (int i = 0; i < relatedSkills.size(); i++) {
                sb.append(relatedSkills.get(i));
                if (i < relatedSkills.size() - 1) sb.append(", ");
            }
        }
        relatedSkillsTextView.setText(sb.toString());
    }

    protected void createNotification(Task task){
        getController().removeTaskNotification(task);
        getController().addTaskNotification(task);
    }

    private void showDatePickerDialog(){
        final View dialogView = View.inflate(getContext(), R.layout.date_picker_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.YEAR, datePicker.getYear());
                cal.set(Calendar.MONTH, datePicker.getMonth());
                cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                date = cal.getTime();
                showTimePickerDialog();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private void showTimePickerDialog(){
        final View dialogView = View.inflate(getContext(), R.layout.time_picker_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        final CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.whole_day_checkbox);
        timePicker.setIs24HourView(true);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timePicker.setEnabled(!isChecked);
                timePicker.setAlpha(isChecked ? 0.3f : 1f);
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkBox.isChecked()) {
                    dateMode = DateMode.WHOLE_DAY;
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    date = cal.getTime();
                    dateMode = DateMode.SPECIFIC_TIME;
                }
                updateDateView();
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private void showCustomRepeatDialog(){
        final View dialogView = View.inflate(getContext(), R.layout.specific_repeat_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setCancelable(false).create();
        final Switch onOffButton = (Switch) dialogView.findViewById(R.id.turn_off_repeat_button);
        final Spinner modeSpinner = (Spinner) dialogView.findViewById(R.id.repeat_mode_spinner);
        final Spinner repeatabilitySpinner = (Spinner) dialogView.findViewById(R.id.repeatability_spinner);
        final View repeatGroup = dialogView.findViewById(R.id.specific_repeat_group);
        final View daysOfWeek = dialogView.findViewById(R.id.days_of_week);
        final TextView timeUnit = (TextView) dialogView.findViewById(R.id.time_unit);
        final EditText repeatTimesEditText = (EditText) dialogView.findViewById(R.id.repeat_times);
        final EditText repeatIndexEditText = (EditText) dialogView.findViewById(R.id.repeat_index_edit_text);

        onOffButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                repeatGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                modeSpinner.setEnabled(isChecked);
            }
        });

        //setting up repeatability spinner
        String[] repeatabilityList = getResources().getStringArray(R.array.repeatability_spinner_array);
        ArrayAdapter<String> repeatAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, repeatabilityList);
        repeatabilitySpinner.setAdapter(repeatAdapter);
        repeatabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //always
                        repeatTimesEditText.setVisibility(View.GONE);
                        break;
                    case 1: //times
                        repeatTimesEditText.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //setting up mode spinner
        String[] modeList = new String[5];
        String[] repeatTimesArray = getResources().getStringArray(R.array.repeat_pick_array);
        modeList[0] = getString(R.string.just_repeat);
        for (int i = 1; i < modeList.length; i++) {
            modeList[i] = repeatTimesArray[i];
        }
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, modeList);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //no date, just repeat
                        dialogView.findViewById(R.id.repeat_every_layout).setVisibility(View.GONE);
                        daysOfWeek.setVisibility(View.GONE);
                        break;
                    case 1: //everyday
                        dialogView.findViewById(R.id.repeat_every_layout).setVisibility(View.VISIBLE);
                        timeUnit.setText(R.string.day);
                        daysOfWeek.setVisibility(View.GONE);
                        break;
                    case 2: //every week
                        dialogView.findViewById(R.id.repeat_every_layout).setVisibility(View.VISIBLE);
                        timeUnit.setText(R.string.week);
                        daysOfWeek.setVisibility(View.VISIBLE);
                        break;
                    case 3: //every month
                        dialogView.findViewById(R.id.repeat_every_layout).setVisibility(View.VISIBLE);
                        timeUnit.setText(R.string.month);
                        daysOfWeek.setVisibility(View.GONE);
                        break;
                    case 4: //every year
                        dialogView.findViewById(R.id.repeat_every_layout).setVisibility(View.VISIBLE);
                        timeUnit.setText(R.string.year);
                        daysOfWeek.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!onOffButton.isChecked()) {
                    repeatTextView.setText(R.string.task_repeat_do_not_repeat);
                    repeatMode = RepeatMode.DO_NOT_REPEAT;
                    repeatability = 1;
                } else {
                    int selectedTimeMode = modeSpinner.getSelectedItemPosition();
                    String repeatIndexString = repeatIndexEditText.getText().toString();
                    String repeatTimesString = repeatTimesEditText.getText().toString();
                    if (repeatIndexString.isEmpty()) repeatIndexString = "1";
                    if (repeatTimesString.isEmpty()) repeatTimesString = "1";
                    switch (selectedTimeMode) {
                        case 0: //simple repeat
                            repeatMode = RepeatMode.SIMPLE_REPEAT;
                            break;
                        case 1: //everyday
                            repeatMode = RepeatMode.EVERY_NTH_DAY;
                            repeatIndex = Integer.parseInt(repeatIndexString);
                            break;
                        case 2: //every week
                            repeatMode = RepeatMode.DAYS_OF_NTH_WEEK;
                            repeatIndex = Integer.parseInt(repeatIndexString);
                            repeatDaysOfWeek = new Boolean[7];
                            for (int i = 0; i < repeatDaysOfWeek.length; i++) {
                                repeatDaysOfWeek[i] = false;
                            }
                            if (((CheckBox) alertDialog.findViewById(R.id.sunday_checkbox)).isChecked())
                                repeatDaysOfWeek[0] = true;
                            if (((CheckBox) alertDialog.findViewById(R.id.monday_checkbox)).isChecked())
                                repeatDaysOfWeek[1] = true;
                            if (((CheckBox) alertDialog.findViewById(R.id.tuesday_checkbox)).isChecked())
                                repeatDaysOfWeek[2] = true;
                            if (((CheckBox) alertDialog.findViewById(R.id.wednesday_checkbox)).isChecked())
                                repeatDaysOfWeek[3] = true;
                            if (((CheckBox) alertDialog.findViewById(R.id.thursday_checkbox)).isChecked())
                                repeatDaysOfWeek[4] = true;
                            if (((CheckBox) alertDialog.findViewById(R.id.friday_checkbox)).isChecked())
                                repeatDaysOfWeek[5] = true;
                            if (((CheckBox) alertDialog.findViewById(R.id.saturday_checkbox)).isChecked())
                                repeatDaysOfWeek[6] = true;
                            break;
                        case 3: //every month
                            repeatMode = RepeatMode.EVERY_NTH_MONTH;
                            repeatIndex = Integer.parseInt(repeatIndexString);
                            break;
                        case 4: //every year
                            repeatMode = RepeatMode.EVERY_NTH_YEAR;
                            repeatIndex = Integer.parseInt(repeatIndexString);
                            break;
                    }
                    repeatability = repeatabilitySpinner.getSelectedItemPosition() == 0 ?
                            -1 : Integer.parseInt(repeatTimesString);
                    if (repeatability == 0) repeatability = -1;
                    if (repeatIndex == 0) repeatIndex = 1;
                    if (repeatMode == RepeatMode.DAYS_OF_NTH_WEEK &&
                            repeatDaysOfWeek != null && !(Arrays.asList(repeatDaysOfWeek)).contains(true)) {
                        repeatMode = RepeatMode.DO_NOT_REPEAT;
                    }
                    updateRepeatView();
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

        //setup detailed repeat dialog
        switch (repeatMode) {
            case RepeatMode.DO_NOT_REPEAT:
                onOffButton.setChecked(false);
            case RepeatMode.SIMPLE_REPEAT:
                modeSpinner.setSelection(0);
                break;
            case RepeatMode.EVERY_NTH_DAY:
                modeSpinner.setSelection(1);
                break;
            case RepeatMode.DAYS_OF_NTH_WEEK:
                modeSpinner.setSelection(2);
                daysOfWeek.setVisibility(View.VISIBLE);
                ((CheckBox) alertDialog.findViewById(R.id.sunday_checkbox)).setChecked(repeatDaysOfWeek[0]);
                ((CheckBox) alertDialog.findViewById(R.id.monday_checkbox)).setChecked(repeatDaysOfWeek[1]);
                ((CheckBox) alertDialog.findViewById(R.id.tuesday_checkbox)).setChecked(repeatDaysOfWeek[2]);
                ((CheckBox) alertDialog.findViewById(R.id.wednesday_checkbox)).setChecked(repeatDaysOfWeek[3]);
                ((CheckBox) alertDialog.findViewById(R.id.thursday_checkbox)).setChecked(repeatDaysOfWeek[4]);
                ((CheckBox) alertDialog.findViewById(R.id.friday_checkbox)).setChecked(repeatDaysOfWeek[5]);
                ((CheckBox) alertDialog.findViewById(R.id.saturday_checkbox)).setChecked(repeatDaysOfWeek[6]);
                break;
            case RepeatMode.EVERY_NTH_MONTH:
                modeSpinner.setSelection(3);
                break;
            case RepeatMode.EVERY_NTH_YEAR:
                modeSpinner.setSelection(4);
                break;
        }
        repeatIndexEditText.setText(String.valueOf(repeatIndex));
        if (repeatability > 0) {
            repeatabilitySpinner.setSelection(1);
            repeatTimesEditText.setText(String.valueOf(repeatability));
        } else {
            repeatabilitySpinner.setSelection(0);
        }
    }

    private void showCustomNotifyDialog(){
        final View dialogView = View.inflate(getContext(), R.layout.specific_notify_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.notify_custom_dialog_title)
                .create();
        final EditText notifyEditText = (EditText) dialogView.findViewById(R.id.notify_time_edit_text);
        final RadioGroup notifyRadioGroup = (RadioGroup) dialogView.findViewById(R.id.notify_radio_group);

        notifyEditText.setText("1");
        notifyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int typedValue;
                if (s.toString().isEmpty()) {
                    typedValue = 1;
                } else {
                    typedValue = Integer.parseInt(s.toString());
                }
                if (typedValue == 0) typedValue = 1;
                if (typedValue > notifyEditTextMaxValue)
                    notifyEditText.setText(String.valueOf(notifyEditTextMaxValue));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        notifyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                ((RadioButton)group.getChildAt(0)).setText(R.string.notify_minutes);
                ((RadioButton)group.getChildAt(1)).setText(R.string.notify_hours);
                ((RadioButton)group.getChildAt(2)).setText(R.string.notify_days);
                ((RadioButton)group.getChildAt(3)).setText(R.string.notify_weeks);
                int idx = group.indexOfChild(checkedRadioButton);
                switch (idx) {
                    case 0: //min
                        checkedRadioButton.setText(R.string.notify_minutes_checked);
                        notifyEditTextMaxValue = 600;
                        break;
                    case 1: //hour
                        checkedRadioButton.setText(R.string.notify_hours_checked);
                        notifyEditTextMaxValue = 120;
                        break;
                    case 2: //day
                        checkedRadioButton.setText(R.string.notify_days_checked);
                        notifyEditTextMaxValue = 28;
                        break;
                    case 3: //week
                        checkedRadioButton.setText(R.string.notify_weeks_checked);
                        notifyEditTextMaxValue = 4;
                        break;
                }
                String notifyString = notifyEditText.getText().toString();
                if (notifyString.isEmpty()) {
                    notifyEditText.setText("1");
                    notifyString = "1";
                }
                int typedValue = Integer.parseInt(notifyString);
                if (typedValue > notifyEditTextMaxValue) {
                    notifyEditText.setText(String.valueOf(notifyEditTextMaxValue));
                }
                if (typedValue == 0) {
                    notifyEditText.setText("1");
                }
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String typedString = notifyEditText.getText().toString();
                if (typedString.isEmpty()) typedString = "0";
                int typedValue = Integer.parseInt(typedString);
                int checkedRadioButton = notifyRadioGroup.getCheckedRadioButtonId();
                int selectedRadioButton = notifyRadioGroup.indexOfChild(notifyRadioGroup.findViewById(checkedRadioButton));
                long timeUnit = 0;
                switch (selectedRadioButton) {
                    case 0: //min
                        timeUnit = TimeUnitUtils.MINUTE;
                        break;
                    case 1: //hour
                        timeUnit = TimeUnitUtils.HOUR;
                        break;
                    case 2: //day
                        timeUnit = TimeUnitUtils.DAY;
                        break;
                    case 3: //week
                        timeUnit = TimeUnitUtils.WEEK;
                        break;
                }
                notifyDelta = typedValue * timeUnit;
                updateNotifyView();
                alertDialog.dismiss();
            }
        });
        ((RadioButton)notifyRadioGroup.getChildAt(0)).setChecked(true);
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    @Override
    public boolean isDependableDataAvailable() {
        return true;
    }

    @SuppressLint("ValidFragment")
    public class RelatedSkillSelectionDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            List<Skill> allSkills = getController().getAllSkills();
            Collections.sort(allSkills, Skill.TITLE_COMPARATOR);
            String[] skillNames = new String[allSkills.size()];
            boolean[] skillStates = new boolean[allSkills.size()];

            for (int i = 0; i < allSkills.size(); i++) {
                skillNames[i] = allSkills.get(i).getTitle();
                skillStates[i] = relatedSkills.contains(allSkills.get(i).getTitle());
            }

            final String[] items = skillNames;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.skill_choosing)
                    .setMultiChoiceItems(items, skillStates,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                                    if (isChecked){
                                        relatedSkills.add(items[item]);
                                    } else {
                                        relatedSkills.remove(items[item]);
                                    }
                                }
                            })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateRelatedSkillsView();
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false);

            return builder.create();
        }
    }
}
