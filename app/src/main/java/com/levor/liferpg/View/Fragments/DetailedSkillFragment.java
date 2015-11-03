package com.levor.liferpg.View.Fragments;


import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpg.Adapters.TasksAdapter;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;
import com.levor.liferpg.View.DetailedTaskActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailedSkillFragment extends DefaultFragment {

    public final static String SELECTED_SKILL_TITLE_TAG = "selected_skill_title_tag";
    private TextView skillTitleTV;
    private TextView keyCharTV;
    private TextView levelValue;
    private TextView sublevelValue;
    private TextView toNextLevel;
    private ListView listView;

    private Skill currentSkill;
    private ArrayList<String> currentTasks;
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
        currentSkill = getController().getSkillByTitle(getArguments().getString(SELECTED_SKILL_TITLE_TAG));
        getActivity().setTitle(currentSkill.getTitle() + " skill details");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTaskTitle = currentTasks.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailedTaskFragment.SELECTED_TASK_TITLE_TAG, selectedTaskTitle);
                Fragment fragment = new DetailedTaskFragment();
                getCurrentActivity().showChildFragment(fragment, bundle);
            }
        });
        createAdapter();
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

    private void createAdapter(){
        ArrayList<Task> tasks = getController().getTasksBySkill(currentSkill);
        ArrayList<String> titles = new ArrayList<>();
        for (Task t: tasks){
            titles.add(t.getTitle());
        }
        currentTasks = titles;
        adapter = new TasksAdapter(getActivity(), titles);
        listView.setAdapter(adapter);
    }

    private void updateSkillDetails(){
        skillTitleTV.setText(currentSkill.getTitle());
        keyCharTV.setText(currentSkill.getKeyCharacteristic().getTitle());
        levelValue.setText(" " + currentSkill.getLevel());
        sublevelValue.setText(" " + currentSkill.getSublevel());
        toNextLevel.setText(" " + (currentSkill.getLevel() - currentSkill.getSublevel()));
    }
}
