package com.levor.liferpg.View.Fragments.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.levor.liferpg.Adapters.TaskAddingAdapter;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddTaskFragment extends DefaultFragment {
    public static final String RECEIVED_SKILL_TITLE_TAG = "received_skill_tag";

    private final String TASK_TITLE_TAG = "task_title_tag";
    private final String RELATED_SKILLS_TAG = "related_skills_tag";

    protected EditText newTaskTitleEditText;
    private ListView relatedSkillListView;
    private Button addSkillButton;

    protected ArrayList<String> relatedSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        newTaskTitleEditText = (EditText) view.findViewById(R.id.new_task_title_edit_text);

        relatedSkillListView = (ListView) view.findViewById(R.id.related_skills_to_add);
        addSkillButton = (Button) view.findViewById(R.id.add_related_skill);
        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item
                        , getController().getSkillsTitlesAndLevels().keySet().toArray(new String[getController().getSkillsTitlesAndLevels().size()]));

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
        if (savedInstanceState != null) {
            newTaskTitleEditText.setText(savedInstanceState.getString(TASK_TITLE_TAG));
            relatedSkills = savedInstanceState.getStringArrayList(RELATED_SKILLS_TAG);
        }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_task:
                String title = newTaskTitleEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getActivity(), "Task title can't be empty", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (getController().getTaskByTitle(title) != null) {
                    createIdenticalTaskRequestDialog(title);
                    return true;
                } else {
                    finishTask(title, "Task added");
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
        outState.putSerializable(TASK_TITLE_TAG, newTaskTitleEditText.getText().toString());
        outState.putSerializable(RELATED_SKILLS_TAG, relatedSkills);
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
                        finishTask(title, "Task added");
                    }
                })
                .show();

    }

    protected void finishTask(String title, String message){
        getController().createNewTask(title, relatedSkills);
        getCurrentActivity().saveAppData();
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
}
