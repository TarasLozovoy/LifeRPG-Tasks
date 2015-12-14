package com.levor.liferpg.View.Fragments.Tasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.levor.liferpg.Adapters.TaskAddingAdapter;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddTaskFragment extends DefaultFragment {
    public static final String RECEIVED_SKILL_TITLE_TAG = "received_skill_tag";

    private final String TASK_TITLE_TAG = "task_title_tag";
    private final String RELATED_SKILLS_TAG = "related_skills_tag";
    private final String REPEAT_TAG = "repeat_tag";
    private final String DIFFICULTY_TAG = "difficulty_tag";
    private final String IMPORTANCE_TAG = "importance_tag";
    private final String DATE_TAG = "date_tag";

    protected EditText taskTitleEditText;
    protected EditText taskRepeatEditText;
    protected Spinner difficultySpinner;
    protected Spinner importanceSpinner;
    private ListView relatedSkillListView;
    private Button addSkillButton;
    private Button dateButton;

    Date date;
    protected ArrayList<String> relatedSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        taskTitleEditText = (EditText) view.findViewById(R.id.task_title_edit_text);
        relatedSkillListView = (ListView) view.findViewById(R.id.related_skills_to_add);
        addSkillButton = (Button) view.findViewById(R.id.add_related_skill);
        dateButton = (Button) view.findViewById(R.id.date_button);
        taskRepeatEditText = (EditText) view.findViewById(R.id.task_repeat_times_edit_text);
        difficultySpinner = (Spinner) view.findViewById(R.id.difficulty_spinner);
        importanceSpinner = (Spinner) view.findViewById(R.id.importance_spinner);

        setupSpinners();
        if (savedInstanceState != null) {
            taskTitleEditText.setText(savedInstanceState.getString(TASK_TITLE_TAG));
            relatedSkills = savedInstanceState.getStringArrayList(RELATED_SKILLS_TAG);
            taskRepeatEditText.setText(savedInstanceState.getString(REPEAT_TAG));
            difficultySpinner.setSelection(savedInstanceState.getInt(DIFFICULTY_TAG));
            importanceSpinner.setSelection(savedInstanceState.getInt(IMPORTANCE_TAG));
            date = new Date (savedInstanceState.getLong(DATE_TAG));
        } else {
            date = new Date();
        }
        setupDateButton(date);

        registerListeners(view);
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
            case R.id.create_task:
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

        String repeatTimesString = taskRepeatEditText.getText().toString();
        if (repeatTimesString.isEmpty()) repeatTimesString = "-1";
        int repeat = Integer.parseInt(repeatTimesString);

        getController().createNewTask(title, repeat, difficulty, importance, date, relatedSkills);

        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }

    private void setupListView(){
        relatedSkillListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                relatedSkills.remove(position);
                updateListView();
            }
        });
        updateListView();
    }

    private void registerListeners(final View view) {
        taskRepeatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item
                        , getController().getSkillsTitles());

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

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragmentTrans();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
    }

    private void setupSpinners(){
        difficultySpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.select_dialog_item,
                getResources().getStringArray(R.array.difficulties_array)));
        importanceSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.select_dialog_item,
                getResources().getStringArray(R.array.importance_array)));
    }

    protected void setupDateButton(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR, 1);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 1);
        this.date = cal.getTime();
        String formatting = Task.getFormatting();
        dateButton.setText(DateFormat.format(formatting, this.date));
    }

    public class SelectDateFragmentTrans extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public SelectDateFragmentTrans(){}

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
            cal.set(yy, mm, dd);
            setupDateButton(cal.getTime());
        }


    }
}
