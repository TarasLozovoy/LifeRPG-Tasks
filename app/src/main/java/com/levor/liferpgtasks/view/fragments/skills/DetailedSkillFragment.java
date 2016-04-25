package com.levor.liferpgtasks.view.fragments.skills;


import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpgtasks.adapters.TasksAdapter;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DataDependantFrament;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.characteristics.EditCharacteristicFragment;
import com.levor.liferpgtasks.view.fragments.tasks.AddTaskFragment;
import com.levor.liferpgtasks.view.fragments.tasks.DetailedTaskFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetailedSkillFragment extends DataDependantFrament {

    public final static String SELECTED_SKILL_UUID_TAG = "selected_skill_UUID_tag";
    private TextView skillTitleTV;
    private TextView keyCharTV;
    private TextView levelValue;
    private TextView sublevelValue;
    private TextView toNextLevel;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private Skill currentSkill;
    private List<String> currentTasks;
    private TasksAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_deatiled_skill, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.related_tasks);
//        View header = LayoutInflater.from(getCurrentActivity()).inflate(R.layout.detailed_skill_header, null);
        skillTitleTV = (TextView) v.findViewById(R.id.skill_title);
        keyCharTV = (TextView) v.findViewById(R.id.key_char);
        levelValue = (TextView) v.findViewById(R.id.level_value);
        sublevelValue = (TextView) v.findViewById(R.id.sublevel_value);
        toNextLevel = (TextView) v.findViewById(R.id.to_next_level_value);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        Button addRelatedTasks = (Button) v.findViewById(R.id.related_tasks_button);

        addRelatedTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable(AddTaskFragment.RECEIVED_SKILL_TITLE_TAG, currentSkill.getTitle());
                getCurrentActivity().showChildFragment(new AddTaskFragment(), b);
            }
        });
        fab.setOnClickListener(new FabClickListener());

        UUID id = (UUID)getArguments().get(SELECTED_SKILL_UUID_TAG);
        currentSkill = getController().getSkillByID(id);
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.skill));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);

        setupListView();
        updateSkillDetails();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Detailed Skill Fragment");
    }

    private void setupListView(){
        List<Task> tasks = getController().getTasksBySkill(currentSkill);
        List<String> titles = new ArrayList<>();
        for (Task t: tasks){
            titles.add(t.getTitle());
        }
        currentTasks = titles;
        adapter = new TasksAdapter(titles, getCurrentActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                currentSkill = getController().getSkillByTitle(currentSkill.getTitle());
                updateSkillDetails();
            }
        });
    }

    private void updateSkillDetails(){
        DecimalFormat df = new DecimalFormat("#.##");
        String sublevelString = df.format(currentSkill.getSublevel());
        String toNextLevelString = df.format(currentSkill.getLevel() - currentSkill.getSublevel());

        skillTitleTV.setText(currentSkill.getTitle());
        StringBuilder sb = new StringBuilder();
        for (Characteristic ch : currentSkill.getKeyCharacteristicsList()) {
            sb.append(ch.getTitle())
                    .append(", ");
        }
        if (sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length() - 1);
        }
        keyCharTV.setText(sb.toString());
        levelValue.setText(String.valueOf(currentSkill.getLevel()));
        sublevelValue.setText(sublevelString);
        toNextLevel.setText(toNextLevelString);
    }

    @Override
    public boolean isDependableDataAvailable() {
        UUID id = (UUID)getArguments().get(SELECTED_SKILL_UUID_TAG);
        Skill skill = getController().getSkillByID(id);
        return skill != null;
    }

    private class FabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();
            b.putSerializable(EditSkillFragment.EDIT_SKILL_UUID_TAG, currentSkill.getId());
            DefaultFragment f = new EditSkillFragment();
            getCurrentActivity().showChildFragment(f, b);
        }
    }
}
