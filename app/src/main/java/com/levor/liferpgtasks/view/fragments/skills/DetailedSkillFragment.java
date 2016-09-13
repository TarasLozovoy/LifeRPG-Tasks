package com.levor.liferpgtasks.view.fragments.skills;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.levor.liferpgtasks.Utils.TextUtils;
import com.levor.liferpgtasks.adapters.TasksAdapter;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DataDependantFrament;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.tasks.AddTaskFragment;
import com.levor.liferpgtasks.view.fragments.tasks.EditTaskFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetailedSkillFragment extends DataDependantFrament {

    public final static String SELECTED_SKILL_UUID_TAG = "selected_skill_UUID_tag";
    private static final int UNDO_CONTEXT_MENU_ITEM = 0;
    private static final int EDIT_CONTEXT_MENU_ITEM = 1;
    private static final int DELETE_CONTEXT_MENU_ITEM = 2;
    private TextView skillTitleTV;
    private TextView keyCharTV;
    private TextView levelValue;
    private TextView sublevelValue;
    private TextView toNextLevel;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private View header;

    private Skill currentSkill;
    private List<String> currentTasksTitles;
    private TasksAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detailed_skill, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.related_tasks_recycler_view);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        header = LayoutInflater.from(getCurrentActivity()).inflate(R.layout.detailed_skill_header, null);
        skillTitleTV = (TextView) header.findViewById(R.id.skill_title);
        keyCharTV = (TextView) header.findViewById(R.id.key_char);
        levelValue = (TextView) header.findViewById(R.id.level_value);
        sublevelValue = (TextView) header.findViewById(R.id.sublevel_value);
        toNextLevel = (TextView) header.findViewById(R.id.to_next_level_value);
        Button addRelatedTasks = (Button) header.findViewById(R.id.related_tasks_button);

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

        updateUI();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Detailed Skill Fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recyclerView != null) {
            unregisterForContextMenu(recyclerView);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.related_tasks_recycler_view){
            String selectedTitle = currentTasksTitles.get(((TasksAdapter) recyclerView.getAdapter()).getPosition());
            Task selectedTask = getController().getTaskByTitle(selectedTitle);
            menu.setHeaderTitle(selectedTitle);
            if (selectedTask.isUndonable()) {
                menu.add(0, UNDO_CONTEXT_MENU_ITEM, UNDO_CONTEXT_MENU_ITEM, R.string.undo);
            }
            menu.add(0, EDIT_CONTEXT_MENU_ITEM, EDIT_CONTEXT_MENU_ITEM, R.string.edit_task);
            menu.add(0, DELETE_CONTEXT_MENU_ITEM, DELETE_CONTEXT_MENU_ITEM, R.string.remove);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String selectedTitle = currentTasksTitles.get(((TasksAdapter) recyclerView.getAdapter()).getPosition());
        final Task currentTask = getController().getTaskByTitle(selectedTitle);

        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case UNDO_CONTEXT_MENU_ITEM:
                getController().undoTask(currentTask);
                updateUI();
                return true;
            case EDIT_CONTEXT_MENU_ITEM:
                DefaultFragment f = new EditTaskFragment();
                Bundle b = new Bundle();
                b.putSerializable(EditTaskFragment.CURRENT_TASK_TITLE_TAG, selectedTitle);
                getCurrentActivity().showChildFragment(f, b);
                return true;
            case DELETE_CONTEXT_MENU_ITEM:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(currentTask.getTitle())
                        .setMessage(getString(R.string.removing_task_description))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getController().removeTask(currentTask);
                                dialog.dismiss();
                                updateUI();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
                return true;
        }

        return false;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setupRecyclerView();
        updateSkillDetails();
    }

    private void setupRecyclerView(){
        List<Task> tasks = getController().getTasksBySkill(currentSkill);
        List<String> titles = new ArrayList<>();
        for (Task t: tasks){
            titles.add(t.getTitle());
        }
        currentTasksTitles = titles;
        adapter = new TasksAdapter(titles, getCurrentActivity(), TasksAdapter.Mode.REGULAR);
        adapter.setHeader(header);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                currentSkill = getController().getSkillByTitle(currentSkill.getTitle());
                updateSkillDetails();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        registerForContextMenu(recyclerView);
    }

    private void updateSkillDetails(){
        String sublevelString = TextUtils.DECIMAL_FORMAT.format(currentSkill.getSublevel());
        String toNextLevelString = TextUtils.DECIMAL_FORMAT.format(currentSkill.getLevel() - currentSkill.getSublevel());

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
