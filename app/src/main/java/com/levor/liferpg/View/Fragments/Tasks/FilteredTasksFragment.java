package com.levor.liferpg.View.Fragments.Tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.levor.liferpg.Adapters.TasksAdapter;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FilteredTasksFragment extends DefaultFragment {
    public static final String FILTER_ARG = "filter_arg";

    public static final int ALL = 0;
    public static final int INFINITE = 1;
    public static final int SIMPLE = 2;
    public static final int DONE = 3;

    private int filter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtered_tasks, container, false);
        filter = getArguments().getInt(FILTER_ARG);

        listView = (ListView) view.findViewById(R.id.listViewTasks);
        setupListView();
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Tasks");
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        return view;
    }

    private void setupListView() {
        List<Task> tasks = getController().getAllTasks();
        List<String> filteredTasks = new ArrayList<>();
        for (Task t : tasks) {
            switch (filter) {
                case ALL:
                    filteredTasks.add(t.getTitle());
                    break;
                case INFINITE:
                    if (t.getRepeatability() < 0) {
                        filteredTasks.add(t.getTitle());
                    }
                    break;
                case SIMPLE:
                    if (t.getRepeatability() > 0) {
                        filteredTasks.add(t.getTitle());
                    }
                    break;
                case DONE:
                    if (t.getRepeatability() == 0) {
                        filteredTasks.add(t.getTitle());
                    }
                    break;
                default:
                    throw new RuntimeException("Not supported filter.");
            }
        }

        TasksAdapter adapter = new TasksAdapter(filteredTasks, getCurrentActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTaskTitle = getController().getAllTasks().get(position).getTitle();
                UUID taskID = getController().getTaskByTitle(selectedTaskTitle).getId();
                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailedTaskFragment.SELECTED_TASK_UUID_TAG, taskID);
                getCurrentActivity().showChildFragment(new DetailedTaskFragment(), bundle);
            }
        });
    }

    @Override
    protected void updateUI() {
        setupListView();
    }
}
