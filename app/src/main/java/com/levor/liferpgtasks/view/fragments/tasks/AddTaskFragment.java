package com.levor.liferpgtasks.view.fragments.tasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.adapters.TaskAddingAdapter;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    protected EditText taskRepeatEditText;
    protected CheckBox repeatCheckbox;
    protected CheckBox notifyCheckbox;
    protected Spinner difficultySpinner;
    protected Spinner importanceSpinner;
    private ListView relatedSkillListView;
    private Button dateButton;
    private Button timeButton;
    protected LinearLayout repeatDetailedLayout;

    Date date;
    protected ArrayList<String> relatedSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        taskTitleEditText = (EditText) view.findViewById(R.id.task_title_edit_text);
        relatedSkillListView = (ListView) view.findViewById(R.id.related_skills_to_add);
        dateButton = (Button) view.findViewById(R.id.date_button);
        timeButton = (Button) view.findViewById(R.id.time_button);
        taskRepeatEditText = (EditText) view.findViewById(R.id.task_repeat_times_edit_text);
        difficultySpinner = (Spinner) view.findViewById(R.id.difficulty_spinner);
        importanceSpinner = (Spinner) view.findViewById(R.id.importance_spinner);
        repeatCheckbox = (CheckBox) view.findViewById(R.id.repeat_checkbox);
        notifyCheckbox = (CheckBox) view.findViewById(R.id.notify_checkbox);
        repeatDetailedLayout = (LinearLayout) view.findViewById(R.id.task_repeat_detailed_linear_layout);

        setupSpinners();
        registerListeners(view);
        if (savedInstanceState != null) {
            taskTitleEditText.setText(savedInstanceState.getString(TASK_TITLE_TAG));
            relatedSkills = savedInstanceState.getStringArrayList(RELATED_SKILLS_TAG);
            taskRepeatEditText.setText(savedInstanceState.getString(REPEAT_TAG));
            difficultySpinner.setSelection(savedInstanceState.getInt(DIFFICULTY_TAG));
            importanceSpinner.setSelection(savedInstanceState.getInt(IMPORTANCE_TAG));
            date = new Date (savedInstanceState.getLong(DATE_TAG));
            notifyCheckbox.setChecked(savedInstanceState.getBoolean(NOTIFY_TAG, true));
            repeatCheckbox.setChecked(savedInstanceState.getBoolean(REPEAT_CHECKBOX_TAG, true));
        } else {
            date = new Date();
            notifyCheckbox.setChecked(true);
        }
        setupDateTimeButtons(date);
        setupListView();

        String skillTitle;
        if (getArguments() != null && (skillTitle = getArguments().getString(RECEIVED_SKILL_TITLE_TAG)) != null) {
            relatedSkills.add(skillTitle);
            updateListView();
        }
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Create new task");
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
        imm.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
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
                    Snackbar.make(getView(), "Task title can't be empty", Snackbar.LENGTH_LONG).show();
                    return true;
                }
                if (getController().getTaskByTitle(title) != null) {
                    createIdenticalTaskRequestDialog(title);
                    return true;
                } else {
                    finishTask(title, "Task added: " + title);
                }
                return true;
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(TASK_TITLE_TAG, taskTitleEditText.getText().toString());
        outState.putSerializable(RELATED_SKILLS_TAG, relatedSkills);
        outState.putSerializable(REPEAT_TAG, taskRepeatEditText.getText().toString());
        outState.putSerializable(DIFFICULTY_TAG, difficultySpinner.getSelectedItemPosition());
        outState.putSerializable(IMPORTANCE_TAG, importanceSpinner.getSelectedItemPosition());
        outState.putSerializable(DATE_TAG, date);
        outState.putSerializable(NOTIFY_TAG, notifyCheckbox.isChecked());
        outState.putSerializable(REPEAT_CHECKBOX_TAG, repeatCheckbox.isChecked());
        super.onSaveInstanceState(outState);
    }

    private void updateListView(){
        relatedSkillListView.setAdapter(new TaskAddingAdapter(getActivity(), relatedSkills));
    }

    protected void createIdenticalTaskRequestDialog(final String title){
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
                        finishTask(title, "Task added: " + title);
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
        closeKeyboard();
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }

    protected void closeKeyboard(){
        View view = getCurrentActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getCurrentActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setupListView(){
        relatedSkillListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                relatedSkills.remove(position);
                updateListView();
            }
        });
        Button footerButton = new Button(getActivity());
        footerButton.setText(R.string.add_skill_to_task);
        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> skillsList = getController().getSkillsTitles();
                skillsList.removeAll(relatedSkills);
                if(skillsList.isEmpty()){
                    Toast.makeText(getContext(), getString(R.string.all_related_skills_added), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.select_dialog_item, skillsList);

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
        relatedSkillListView.addFooterView(footerButton);
        updateListView();
    }

    private void registerListeners(final View view) {
        taskTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getCurrentActivity().showSoftKeyboard(false, getView());
                }
            }
        });
        repeatCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                repeatDetailedLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        taskRepeatEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getCurrentActivity().showSoftKeyboard(false, getView());
                }
            }
        });
        taskRepeatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) s = "-1";
                int repeat = Integer.parseInt(s.toString());
                if (repeat > 999) {
                    taskRepeatEditText.setText(Integer.toString(999));
                    taskRepeatEditText.setSelection(taskRepeatEditText.getText().length());
                    Snackbar.make(view, view.getResources().getString(R.string.max_task_repeat), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragmentTrans();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectTimeFragmentTrans();
                newFragment.show(getFragmentManager(), "TimePickerPicker");
            }
        });
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

    protected void setupDateTimeButtons(Date d){
        setupDateButton(d);
        setupTimeButton(d);
    }

    private void setupDateButton(Date d){
        Calendar newCal = Calendar.getInstance();
        newCal.setTime(d);
        Calendar oldCal = Calendar.getInstance();
        oldCal.setTime(date);
        newCal.set(Calendar.HOUR_OF_DAY, oldCal.get(Calendar.HOUR_OF_DAY));
        newCal.set(Calendar.MINUTE, oldCal.get(Calendar.MINUTE));
        newCal.set(Calendar.SECOND, 0);
        newCal.set(Calendar.MILLISECOND, 0);
        date = newCal.getTime();
        dateButton.setText(DateFormat.format(Task.getDateFormatting(), date));
    }

    private void setupTimeButton(Date d){
        Calendar newCal = Calendar.getInstance();
        newCal.setTime(d);
        Calendar oldCal = Calendar.getInstance();
        oldCal.setTime(date);
        newCal.set(Calendar.YEAR, oldCal.get(Calendar.YEAR));
        newCal.set(Calendar.MONTH, oldCal.get(Calendar.MONTH));
        newCal.set(Calendar.DAY_OF_MONTH, oldCal.get(Calendar.DAY_OF_MONTH));
        newCal.set(Calendar.SECOND, 0);
        newCal.set(Calendar.MILLISECOND, 0);
        date = newCal.getTime();
        timeButton.setText(DateFormat.format(Task.getTimeFormatting(), date));
    }

    protected void createNotification(String taskTitle){
        Task task = getController().getTaskByTitle(taskTitle);
        getController().removeTaskNotification(task);
        getController().addTaskNotification(task);
    }

    protected int getRepeatability(){
        String repeatTimesString;
        if (!repeatCheckbox.isChecked()) {
            repeatTimesString = "1";
        } else {
            repeatTimesString = taskRepeatEditText.getText().toString();
            if (repeatTimesString.isEmpty()) repeatTimesString = "-1";
        }
        return Integer.parseInt(repeatTimesString);
    }

    public class SelectDateFragmentTrans extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(yy, mm, dd);
            setupDateButton(cal.getTime());
        }
    }

    public class SelectTimeFragmentTrans extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hh = calendar.get(Calendar.HOUR_OF_DAY);
            int mm = calendar.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hh, mm, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            setupTimeButton(cal.getTime());
        }
    }
}
