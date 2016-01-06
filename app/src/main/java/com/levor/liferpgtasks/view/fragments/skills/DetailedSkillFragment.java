package com.levor.liferpgtasks.view.fragments.skills;


import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpgtasks.adapters.TasksAdapter;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.tasks.AddTaskFragment;
import com.levor.liferpgtasks.view.fragments.tasks.DetailedTaskFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetailedSkillFragment extends DefaultFragment {

    public final static String SELECTED_SKILL_UUID_TAG = "selected_skill_UUID_tag";
    private TextView skillTitleTV;
    private TextView keyCharTV;
    private TextView levelValue;
    private TextView sublevelValue;
    private TextView toNextLevel;
    private ListView listView;

    private Skill currentSkill;
    private List<String> currentTasks;
    private TasksAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_deatiled_skill, container, false);
        skillTitleTV = (TextView) v.findViewById(R.id.skill_title);
        keyCharTV = (TextView) v.findViewById(R.id.key_char);
        levelValue = (TextView) v.findViewById(R.id.level_value);
        sublevelValue = (TextView) v.findViewById(R.id.sublevel_value);
        toNextLevel = (TextView) v.findViewById(R.id.to_next_level_value);
        listView = (ListView) v.findViewById(R.id.related_tasks);
        UUID id = (UUID)getArguments().get(SELECTED_SKILL_UUID_TAG);
        currentSkill = getController().getSkillByID(id);
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Skill");
        getCurrentActivity().showActionBarHomeButtonAsBack(true);

        setupListView();
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                currentSkill = getController().getSkillByTitle(currentSkill.getTitle());
                updateSkillDetails();
            }
        });
        updateSkillDetails();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_detailed_skill, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_skill:
                Bundle b = new Bundle();
                b.putSerializable(EditSkillFragment.EDIT_SKILL_UUID_TAG, currentSkill.getId());
                DefaultFragment f = new EditSkillFragment();
                getCurrentActivity().showChildFragment(f, b);
                return true;
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Detailed Skill Fragment");
    }

    private void setupListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTaskTitle = currentTasks.get(position);
                UUID taskID = getController().getTaskByTitle(selectedTaskTitle).getId();
                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailedTaskFragment.SELECTED_TASK_UUID_TAG, taskID);
                DefaultFragment fragment = new DetailedTaskFragment();
                getCurrentActivity().showChildFragment(fragment, bundle);
            }
        });

        List<Task> tasks = getController().getTasksBySkill(currentSkill);
        List<String> titles = new ArrayList<>();
        for (Task t: tasks){
            titles.add(t.getTitle());
        }
        currentTasks = titles;
        adapter = new TasksAdapter(titles, getCurrentActivity());
        listView.setAdapter(adapter);
        createFooterView();
    }

    private void updateSkillDetails(){
        skillTitleTV.setText(currentSkill.getTitle());
        keyCharTV.setText(currentSkill.getKeyCharacteristic().getTitle());
        levelValue.setText(" " + currentSkill.getLevel());
        sublevelValue.setText(" " + currentSkill.getSublevel());
        toNextLevel.setText(" " + (currentSkill.getLevel() - currentSkill.getSublevel()));
    }

    private void createFooterView() {
        Button footerButton = new Button(getActivity());
        footerButton.setText(R.string.add_new_task);
        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultFragment f = new AddTaskFragment();
                Bundle b = new Bundle();
                b.putSerializable(AddTaskFragment.RECEIVED_SKILL_TITLE_TAG, currentSkill.getTitle());
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        listView.addFooterView(footerButton);
    }
}
