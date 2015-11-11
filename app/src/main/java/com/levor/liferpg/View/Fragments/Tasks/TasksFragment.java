package com.levor.liferpg.View.Fragments.Tasks;

import android.app.Fragment;
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

import com.levor.liferpg.Adapters.TasksAdapter;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;
import com.levor.liferpg.View.Fragments.Skills.AddSkillFragment;
import com.levor.liferpg.View.Fragments.Tasks.AddTaskFragment;
import com.levor.liferpg.View.Fragments.Tasks.DetailedTaskFragment;

import java.util.UUID;

public class TasksFragment extends DefaultFragment {
    private ListView listView;

    private TasksAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        listView = (ListView) view.findViewById(R.id.listViewTasks);

        createAdapter();
        setupListView();
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Tasks");
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_task:
                getCurrentActivity().showChildFragment(new AddTaskFragment(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createAdapter(){
        adapter = new TasksAdapter(getActivity(), getController().getTasksTitlesAsList());
        listView.setAdapter(adapter);
    }

    private void setupListView(){
        TasksAdapter adapter = new TasksAdapter(getActivity(), getController().getTasksTitlesAsList());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTaskTitle = getController().getTasksTitlesAsList().get(position);
                UUID taskID = getController().getTaskByTitle(selectedTaskTitle).getId();
                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailedTaskFragment.SELECTED_TASK_UUID_TAG, taskID);
                getCurrentActivity().showChildFragment(new DetailedTaskFragment(), bundle);
            }
        });
    }
}
