package com.levor.liferpgtasks.view.fragments.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.adapters.TaskAddingAdapter;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.model.Task.RepeatMode;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.levor.liferpgtasks.model.Task.DateMode;

public class AddTaskFragment extends DefaultFragment {
    public static final String RECEIVED_SKILL_TITLE_TAG = "received_skill_tag";

    public static final String TASK_TITLE_TAG = "task_title_tag";
    private final String RELATED_SKILLS_TAG = "related_skills_tag";
    private final String REPEAT_TAG = "repeat_tag";
    private final String DIFFICULTY_TAG = "difficulty_tag";
    private final String IMPORTANCE_TAG = "importance_tag";
    private final String DATE_TAG = "date_tag";
    private final String NOTIFY_TAG = "notify_tag";
    private final String REPEAT_CHECKBOX_TAG = "repeat_checkbox_tag";

    protected EditText taskTitleEditText;
    protected TextView dateTextView;
    protected View dateView;
    protected TextView notifyTextView;
    protected View notifyView;
    protected TextView repeatTextView;
    protected View repeatView;
    protected TextView difficultyTextView;
    protected View difficultyView;
    protected TextView importanceTextView;
    protected View importanceView;
    protected TextView relatedSkillsTextView;
    protected View relatedSkillsView;

    protected CheckBox notifyCheckbox;
    protected Spinner difficultySpinner;
    protected Spinner importanceSpinner;

    private ListView listView;

    private Button addSkillButton;

    protected Date date;
    protected int dateMode = DateMode.TERMLESS;
    protected int repeatability;
    protected int repeatMode;
    protected int[] repeatDaysOfWeek;
    protected int repeatIndex = 1;
    protected ArrayList<String> relatedSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        listView = (ListView) view;
        View header = LayoutInflater.from(getCurrentActivity()).inflate(R.layout.add_task_header, null);
        taskTitleEditText = (EditText) header.findViewById(R.id.task_title_edit_text);
        dateTextView = (TextView) header.findViewById(R.id.date_time_text_view);
        dateView = header.findViewById(R.id.date_time_layout);
        repeatTextView = (TextView) header.findViewById(R.id.repeat_text_view);
        repeatView = header.findViewById(R.id.repeat_layout);
        notifyTextView = (TextView) header.findViewById(R.id.notification_text_view);
        notifyView = header.findViewById(R.id.notification_layout);
        difficultyTextView = (TextView) header.findViewById(R.id.difficulty_text_view);
        difficultyView = header.findViewById(R.id.difficulty_layout);
        importanceTextView = (TextView) header.findViewById(R.id.importance_text_view);
        importanceView = header.findViewById(R.id.importance_layout);
        relatedSkillsTextView = (TextView) header.findViewById(R.id.related_skills_text_view);
        relatedSkillsView = header.findViewById(R.id.related_skills_layout);

        addSkillButton = (Button) header.findViewById(R.id.add_related_skill_button);
        difficultySpinner = (Spinner) header.findViewById(R.id.difficulty_spinner);
        importanceSpinner = (Spinner) header.findViewById(R.id.importance_spinner);
        notifyCheckbox = (CheckBox) header.findViewById(R.id.notify_checkbox);

        dateTextView.setText(getString(R.string.task_date_termless));
        repeatTextView.setText(getString(R.string.task_repeat_do_not_repeat));
        notifyTextView.setText(getString(R.string.task_add_notification));
        String difficultyString = getString(R.string.difficulty) + " " + getResources().getStringArray(R.array.difficulties_array)[0];
        difficultyTextView.setText(difficultyString);
        String importanceString = getString(R.string.importance) + " " + getResources().getStringArray(R.array.importance_array)[0];
        importanceTextView.setText(importanceString);
        relatedSkillsTextView.setText(R.string.add_skill_to_task);

        setupSpinners();
        registerListeners();
        if (savedInstanceState != null) {
            taskTitleEditText.setText(savedInstanceState.getString(TASK_TITLE_TAG));
            relatedSkills = savedInstanceState.getStringArrayList(RELATED_SKILLS_TAG);
            difficultySpinner.setSelection(savedInstanceState.getInt(DIFFICULTY_TAG));
            importanceSpinner.setSelection(savedInstanceState.getInt(IMPORTANCE_TAG));
            date = new Date (savedInstanceState.getLong(DATE_TAG));
            notifyCheckbox.setChecked(savedInstanceState.getBoolean(NOTIFY_TAG, true));
        } else {
            date = new Date();
            notifyCheckbox.setChecked(true);
        }
        updateDateView();
        listView.addHeaderView(header);
        setupListView();

        String skillTitle;
        if (getArguments() != null && (skillTitle = getArguments().getString(RECEIVED_SKILL_TITLE_TAG)) != null) {
            relatedSkills.add(skillTitle);
            updateListView();
        }
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.add_new_task));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);

        return view;
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
        outState.putSerializable(TASK_TITLE_TAG, taskTitleEditText.getText().toString());
        outState.putSerializable(RELATED_SKILLS_TAG, relatedSkills);
        outState.putSerializable(DIFFICULTY_TAG, difficultySpinner.getSelectedItemPosition());
        outState.putSerializable(IMPORTANCE_TAG, importanceSpinner.getSelectedItemPosition());
        outState.putSerializable(DATE_TAG, date);
        outState.putSerializable(NOTIFY_TAG, notifyCheckbox.isChecked());
        super.onSaveInstanceState(outState);
    }

    private void updateListView(){
        listView.setAdapter(new TaskAddingAdapter(getActivity(), relatedSkills));
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
        int difficulty = difficultySpinner.getSelectedItemPosition();
        int importance = importanceSpinner.getSelectedItemPosition();
        int repeat = getRepeatability();
        boolean notify = notifyCheckbox.isChecked();
        getController().createNewTask(title, repeat, difficulty, importance, date, notify, relatedSkills);

        getController().getGATracker().send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.GA_action))
                .setAction(getString(R.string.GA_add_new_task))
                .build());

        createNotification(title);
        getCurrentActivity().showSoftKeyboard(false, getView());
        Toast.makeText(getCurrentActivity(), message, Toast.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }

    private void setupListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                relatedSkills.remove(position - listView.getHeaderViewsCount());
                updateListView();
            }
        });
        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> skillsList = getController().getSkillsTitles();
                skillsList.removeAll(relatedSkills);
                if (skillsList.isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.all_related_skills_added), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.select_dialog_item, skillsList);

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(getString(R.string.skill_choosing));
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
        updateListView();
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
                                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // - 1 for sunday - 0, monday - 1...
                                repeatDaysOfWeek = new int[7];
                                repeatDaysOfWeek[dayOfWeek] = 1;
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
                Toast.makeText(getContext(), "notifyView", Toast.LENGTH_SHORT).show();
                //todo add new dialog
            }
        });

        difficultyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "difficultyView", Toast.LENGTH_SHORT).show();
                //todo add new dialog
            }
        });

        importanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "importanceView", Toast.LENGTH_SHORT).show();
                //todo add new dialog
            }
        });

        relatedSkillsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "relatedSkillsView", Toast.LENGTH_SHORT).show();
                //todo add new dialog
            }
        });
    }

    private void updateDateView(){
        StringBuilder sb = new StringBuilder();
        if (dateMode == DateMode.TERMLESS){
            sb.append(getString(R.string.task_date_termless));
        } else {
            sb.append(DateFormat.format(Task.getDateFormatting(), date));
            if (dateMode == DateMode.SPECIFIC_TIME){
                sb.append(" ");
                sb.append(DateFormat.format(Task.getTimeFormatting(), date));
            }
        }
        dateTextView.setText(sb.toString());
    }

    private void updateRepeatView(){
        StringBuilder sb = new StringBuilder();
        // TODO: 1/27/16  finish updating
        repeatTextView.setText(sb.toString());
    }

    private void setupSpinners(){
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.difficulties_array,
                R.layout.default_spinner_item);
        difficultyAdapter.setDropDownViewResource(R.layout.default_spinner_drop_down_view);
        difficultySpinner.setAdapter(difficultyAdapter);

        ArrayAdapter<CharSequence> importanceAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.importance_array,
                R.layout.default_spinner_item);
        importanceAdapter.setDropDownViewResource(R.layout.default_spinner_drop_down_view);
        importanceSpinner.setAdapter(importanceAdapter);
    }

    protected void createNotification(String taskTitle){
        Task task = getController().getTaskByTitle(taskTitle);
        getController().removeTaskNotification(task);
        getController().addTaskNotification(task);
    }

    protected int getRepeatability(){
        // TODO: 1/27/16 add method
        return 1;
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
        // TODO: 1/27/16 add dialog
    }
}
