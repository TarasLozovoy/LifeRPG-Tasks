package com.levor.liferpgtasks.view.fragments.tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.TasksAdapter;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilteredTasksFragment extends DefaultFragment{
    public static final String FILTER_ARG = "filter_arg";

    public static final int ALL = 0;
    public static final int INFINITE = 1;
    public static final int SIMPLE = 2;
    public static final int DONE = 3;

    private static final int UNDO_CONTEXT_MENU_ITEM = 0;
    private static final int EDIT_CONTEXT_MENU_ITEM = 1;
    private static final int DELETE_CONTEXT_MENU_ITEM = 2;

    private int filter;
    private String searchQuery = "";
    private RecyclerView recyclerView;
    private TextView emptyList;

    private List<String> sortedTasksTitles = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtered_tasks, container, false);
        filter = getArguments().getInt(FILTER_ARG);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewTasks);
        emptyList = (TextView) view.findViewById(R.id.empty_list);
        if (filter == DONE) emptyList.setText(R.string.empty_done_list_view);
        setupListView();
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(R.string.tasks);
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        isCreated = true;
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sorting:
                String[] sortingVariants = getResources().getStringArray(R.array.sorting_spinner_items);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.select_dialog_item, sortingVariants);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getSorting() != which) {
                            setSorting(which);
                            updateFilteredFragmentsUI();
                        }
                        dialog.dismiss();
                    }
                });
                String currentSorting = getString(R.string.current_sorting) + "\n" + sortingVariants[getSorting()];
                dialog.setTitle(currentSorting);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                setupListView();
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchQuery = "";
                setupListView();
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.recyclerViewTasks){
            String selectedTitle = sortedTasksTitles.get(((TasksAdapter) recyclerView.getAdapter()).getPosition());
            Task selectedTask = getController().getTaskByTitle(selectedTitle);
            menu.setHeaderTitle(selectedTitle);
            menu.add(filter, UNDO_CONTEXT_MENU_ITEM, UNDO_CONTEXT_MENU_ITEM, R.string.undo)
                    .setEnabled(selectedTask.isUndonable());
            menu.add(filter, EDIT_CONTEXT_MENU_ITEM, EDIT_CONTEXT_MENU_ITEM, R.string.edit_task);
            menu.add(filter, DELETE_CONTEXT_MENU_ITEM, DELETE_CONTEXT_MENU_ITEM, R.string.remove);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == filter) {
            String selectedTitle = sortedTasksTitles.get(((TasksAdapter) recyclerView.getAdapter()).getPosition());
            final Task currentTask = getController().getTaskByTitle(selectedTitle);

            int menuItemIndex = item.getItemId();
            switch (menuItemIndex) {
                case UNDO_CONTEXT_MENU_ITEM:
                    getController().undoTask(currentTask);
                    updateFilteredFragmentsUI();
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
                                    updateFilteredFragmentsUI();
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
        switch (getSorting()){
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
            if (!searchQuery.isEmpty()
                    && !t.getTitle().toLowerCase().contains(searchQuery.toLowerCase())) continue;
            switch (filter) {
                case ALL:
                    if (t.getRepeatability() != 0) {
                        sortedTasksTitles.add(t.getTitle());
                    }
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
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            TasksAdapter adapter = new TasksAdapter(sortedTasksTitles, getCurrentActivity());
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    updateFilteredFragmentsUI();
                    super.onChanged();
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            registerForContextMenu(recyclerView);
        }
    }

    @Override
    public void updateUI() {
        setupListView();
    }

    private void updateFilteredFragmentsUI(){
        ((TasksFragment)getParentFragment()).updateChildFragmentsUI();
    }

    private int getSorting() {
        return ((TasksFragment)getParentFragment()).getSorting();
    }

    private void setSorting(int sorting) {
        ((TasksFragment)getParentFragment()).setSorting(sorting);
    }
}
