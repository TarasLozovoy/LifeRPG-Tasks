package com.levor.liferpg.View.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.levor.liferpg.Adapters.TasksAdapter;
import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;
import com.levor.liferpg.View.DetailedTaskActivity;
import com.levor.liferpg.View.MainActivity;

public class TasksFragment extends DefaultFragment {
    private ListView listView;
    private Button addTask;

    private TasksAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        listView = (ListView) view.findViewById(R.id.listViewTasks);
        addTask = (Button) view.findViewById(R.id.add_new_task);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                getCurrentActivity().showChildFragment(new AddTaskFragment());
            }
        });

        createAdapter();
        setupListView();

        return view;
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
                Intent intent = new Intent(getActivity(), DetailedTaskActivity.class);
                intent.putExtra(DetailedTaskActivity.SELECTED_TASK_TITLE_TAG, selectedTaskTitle);
                //TODO
//                getCurrentActivity().showChildFragment(new TasksFragment());
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
