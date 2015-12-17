package com.levor.liferpg.View.Fragments.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.levor.liferpg.Adapters.TasksAdapter;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;
import com.levor.liferpg.View.SortingSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class FilteredTasksFragment extends DefaultFragment {
    public static final String FILTER_ARG = "filter_arg";
    public static final String SHARED_PREFS_TAG = "shared_prefs_tag";
    public static final String SORTING_KEY = "sorting_key";

    public static final int ALL = 0;
    public static final int INFINITE = 1;
    public static final int SIMPLE = 2;
    public static final int DONE = 3;

    private int filter;
    private ListView listView;
    private Spinner orderSpinner;
    private List<String> sortingOrdersList = new ArrayList<>();
    private int sorting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtered_tasks, container, false);
        filter = getArguments().getInt(FILTER_ARG);
        listView = (ListView) view.findViewById(R.id.listViewTasks);
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        sorting = prefs.getInt(SORTING_KEY, Task.SortingOrder.COMPLETION);
        setupListView();
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Tasks");
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        SortingSpinner sortingSpinner = new SortingSpinner(getActivity());
        orderSpinner = sortingSpinner.getSortingSpinner();
        MenuItem item = menu.findItem(R.id.sorting);
        item.setActionView(sortingSpinner);
        if (orderSpinner.getAdapter() == null || orderSpinner.getAdapter().isEmpty()) {
            sortingOrdersList.add(getString(R.string.completion_task_order));
            sortingOrdersList.add(getString(R.string.title_asc_task_order));
            sortingOrdersList.add(getString(R.string.title_desc_task_order));
            sortingOrdersList.add(getString(R.string.importance_ask_task_order));
            sortingOrdersList.add(getString(R.string.importance_desc_task_order));
            sortingOrdersList.add(getString(R.string.difficulty_asc_task_order));
            sortingOrdersList.add(getString(R.string.difficulty_desc_task_order));
            sortingOrdersList.add(getString(R.string.date_asc_task_order));
            sortingOrdersList.add(getString(R.string.date_desc_task_order));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, sortingOrdersList);
            orderSpinner.setAdapter(adapter);
            orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (sorting != position) {
                        sorting = position;
                        setupListView();
                        SharedPreferences prefs = getActivity()
                                .getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                        prefs.edit().putInt(SORTING_KEY, sorting).apply();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }


            });
            orderSpinner.setSelection(sorting);
        }
    }

    private void setupListView() {
        List<Task> tasks = getController().getAllTasks();
        Comparator<Task> comparator = null;
        switch (sorting){
            case Task.SortingOrder.COMPLETION :
                comparator = Task.COMPLETION_TASKS_COMPARATOR;
                break;
            case Task.SortingOrder.TITLE_ASC :
                comparator = Task.TITLE_ASC_TASKS_COMPARATOR;
                break;
            case Task.SortingOrder.TITLE_DESC :
                comparator = Task.TITLE_DESC_TASKS_COMPARATOR;
                break;
            case Task.SortingOrder.IMPORTANCE_ASC :
                comparator = Task.IMPORTANCE_ASC_TASKS_COMPARATOR;
                break;
            case Task.SortingOrder.IMPORTANCE_DESC :
                comparator = Task.IMPORTANCE_DESC_TASKS_COMPARATOR;
                break;
            case Task.SortingOrder.DIFFICULTY_ASC :
                comparator = Task.DIFFICULTY_ASC_TASKS_COMPARATOR;
                break;
            case Task.SortingOrder.DIFFICULTY_DESC :
                comparator = Task.DIFFICULTY_DESC_TASKS_COMPARATOR;
                break;
            case Task.SortingOrder.DATE_ASC :
                comparator = Task.DATE_ASC_TASKS_COMPARATOR;
                break;
            case Task.SortingOrder.DATE_DESC :
                comparator = Task.DATE_DESC_TASKS_COMPARATOR;
                break;

        }
        Collections.sort(tasks, comparator);
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
