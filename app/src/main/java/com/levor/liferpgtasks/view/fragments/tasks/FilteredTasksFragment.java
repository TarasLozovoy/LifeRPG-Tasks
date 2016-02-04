package com.levor.liferpgtasks.view.fragments.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.levor.liferpgtasks.adapters.HighlightStringAdapter;
import com.levor.liferpgtasks.adapters.TasksAdapter;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.SortingSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class FilteredTasksFragment extends DefaultFragment{
    public static final String FILTER_ARG = "filter_arg";
    public static final String SHARED_PREFS_TAG = "shared_prefs_tag";
    public static final String SORTING_KEY = "sorting_key";

    public static final int ALL = 0;
    public static final int INFINITE = 1;
    public static final int SIMPLE = 2;
    public static final int DONE = 3;

    private static final int UNDO_CONTEXT_MENU_ITEM = 0;
    private static final int EDIT_CONTEXT_MENU_ITEM = 1;
    private static final int DELETE_CONTEXT_MENU_ITEM = 2;

    private int filter;
    private ListView listView;
    private TextView emptyList;

    private List<String> sortingOrdersList = new ArrayList<>();
    private List<String> sortedTasksTitles = new ArrayList<>();
    private int sorting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtered_tasks, container, false);
        filter = getArguments().getInt(FILTER_ARG);
        listView = (ListView) view.findViewById(R.id.listViewTasks);
        emptyList = (TextView) view.findViewById(R.id.empty_list);
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        sorting = prefs.getInt(SORTING_KEY, Task.SortingOrder.DATE_DESC);
        setupListView();
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(R.string.tasks);
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_task:
                Bundle b = new Bundle();
                b.putInt(AddTaskFragment.REPEAT_MODE_TAG, filter == INFINITE ?
                        Task.RepeatMode.EVERY_NTH_DAY : Task.RepeatMode.SIMPLE_REPEAT);
                b.putInt(AddTaskFragment.REPEAT_TAG, filter == INFINITE ? -1 : 1);

                getCurrentActivity().showChildFragment(new AddTaskFragment(), b);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem doneItem = menu.findItem(R.id.new_task);
        if (filter == DONE) {
            doneItem.setEnabled(false);
            doneItem.getIcon().setAlpha(75);
        } else {
            doneItem.setEnabled(true);
            doneItem.getIcon().setAlpha(255);
        }

        SortingSpinner sortingSpinner = new SortingSpinner(getActivity());
        Spinner orderSpinner = sortingSpinner.getSortingSpinner();
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
            final HighlightStringAdapter adapter = new HighlightStringAdapter(getCurrentActivity(),
                    R.layout.simple_list_item_1, sortingOrdersList);
            orderSpinner.setAdapter(adapter);
            adapter.setSelection(sorting);
            orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (sorting != position) {
                        sorting = position;
                        adapter.setSelection(sorting);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewTasks){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            String selectedTitle = sortedTasksTitles.get(info.position);
            Task selectedTask = getController().getTaskByTitle(selectedTitle);
            menu.setHeaderTitle(selectedTitle);
            menu.add(Menu.NONE, UNDO_CONTEXT_MENU_ITEM, UNDO_CONTEXT_MENU_ITEM, R.string.undo)
                    .setEnabled(selectedTask.isUndonable());
            menu.add(Menu.NONE, EDIT_CONTEXT_MENU_ITEM, EDIT_CONTEXT_MENU_ITEM, R.string.edit_task);
            menu.add(Menu.NONE, DELETE_CONTEXT_MENU_ITEM, DELETE_CONTEXT_MENU_ITEM, R.string.remove);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String selectedTitle = sortedTasksTitles.get(info.position);
        final Task currentTask = getController().getTaskByTitle(selectedTitle);

        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case UNDO_CONTEXT_MENU_ITEM:
                getController().undoTask(currentTask);
                setupListView();
                currentTask.increaseDateByNDays(-1);
                getController().updateTaskNotification(currentTask);
                getController().updateTask(currentTask);
                return true;
            case EDIT_CONTEXT_MENU_ITEM:
                DefaultFragment f = new EditTaskFragment();
                Bundle b = new Bundle();
                b.putSerializable(EditTaskFragment.CURRENT_TASK_TITLE_TAG, selectedTitle);
                getCurrentActivity().showChildFragment(f, b);
                return true;
            case DELETE_CONTEXT_MENU_ITEM:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(getString(R.string.removing) + " " + currentTask.getTitle())
                        .setMessage(getString(R.string.removing_task_description))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getController().removeTask(currentTask);
                                dialog.dismiss();
                                setupListView();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Tasks Fragment");
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
        sortedTasksTitles = new ArrayList<>();
        for (Task t : tasks) {
            switch (filter) {
                case ALL:
                    sortedTasksTitles.add(t.getTitle());
                    break;
                case INFINITE:
                    if (t.getRepeatability() < 0) {
                        sortedTasksTitles.add(t.getTitle());
                    }
                    break;
                case SIMPLE:
                    if (t.getRepeatability() > 0) {
                        sortedTasksTitles.add(t.getTitle());
                    }
                    break;
                case DONE:
                    if (t.getRepeatability() == 0) {
                        sortedTasksTitles.add(t.getTitle());
                    }
                    break;
                default:
                    throw new RuntimeException("Not supported filter.");
            }
        }

        if (sortedTasksTitles.isEmpty()) {
            emptyList.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            TasksAdapter adapter = new TasksAdapter(sortedTasksTitles, getCurrentActivity());
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    updateUI();
                    super.onChanged();
                }
            });
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedTaskTitle = sortedTasksTitles.get(position);
                    UUID taskID = getController().getTaskByTitle(selectedTaskTitle).getId();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DetailedTaskFragment.SELECTED_TASK_UUID_TAG, taskID);
                    getCurrentActivity().showChildFragment(new DetailedTaskFragment(), bundle);
                }
            });
        }
    }

    @Override
    protected void updateUI() {
        setupListView();
    }
}
