package com.levor.liferpgtasks.view.fragments.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.levor.liferpgtasks.adapters.CustomPagerAdapter;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TasksFragment extends DefaultFragment {
    public static final String SHARED_PREFS_TAG = "shared_prefs_tag";
    public static final String SORTING_KEY = "sorting_key";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    private int sorting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_with_tabs, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all_tasks));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.every_day_tasks));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.simple_tasks));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.finished_tasks));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        sorting = prefs.getInt(SORTING_KEY, Task.SortingOrder.DATE_DESC);

        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_tasks, menu);
    }


    @Override
    public void onStart() {
        super.onStart();
        createViewPager();
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setElevation(0);
        }
        setupFab();
    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setElevation(getResources().getDimension(R.dimen.standard_elevation));
        }
    }

    private void createViewPager(){
        final PagerAdapter adapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setupFab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setupFab();
            }
        });
    }

    public void updateChildFragmentsUI(){
        PagerAdapter adapter = (PagerAdapter) viewPager.getAdapter();
        for (DefaultFragment f : adapter.getFragments().values()) {
            if (f != null && f.isCreated()){
                f.updateUI();
            }
        }
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        prefs.edit().putInt(SORTING_KEY, sorting).apply();
    }

    public void setupFab() {
        fab.show();
        if (viewPager.getCurrentItem() != FilteredTasksFragment.DONE) {
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_black_24dp));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putInt(AddTaskFragment.REPEAT_MODE_TAG, viewPager.getCurrentItem() == FilteredTasksFragment.INFINITE ?
                            Task.RepeatMode.EVERY_NTH_DAY : Task.RepeatMode.SIMPLE_REPEAT);
                    b.putInt(AddTaskFragment.REPEAT_TAG, viewPager.getCurrentItem() == FilteredTasksFragment.INFINITE ? -1 : 1);

                    getCurrentActivity().showChildFragment(new AddTaskFragment(), b);
                }
            });
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_delete_black_24dp));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle(R.string.delete_all_finished_tasks)
                            .setMessage(R.string.delete_all_finished_tasks_message)
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    List<Task> tasks = getController().getAllTasks();
                                    List<Task> finishedTasks = new ArrayList<>();
                                    List<Task> updateNeededTasks = new ArrayList<>();
                                    for (Task t : tasks) {
                                        if (t.isTaskDone()) {
                                            finishedTasks.add(t);
                                        } else if (t.getFinishDate() != null && t.getRepeatability() < 0) {
                                            updateNeededTasks.add(t);
                                        }
                                    }
                                    for (Task t : finishedTasks) {
                                        getController().removeTask(t);
                                    }
                                    updateChildFragmentsUI();
                                }
                            })
                            .show();
                }
            });
        }
    }

    public class PagerAdapter extends CustomPagerAdapter {

        private Map<Integer, DefaultFragment> fragments = new HashMap<>();

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm, NumOfTabs);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle b = new Bundle();
            switch (position) {
                case FilteredTasksFragment.ALL:
                    b.putInt(FilteredTasksFragment.FILTER_ARG, FilteredTasksFragment.ALL);
                    break;
                case FilteredTasksFragment.INFINITE:
                    b.putInt(FilteredTasksFragment.FILTER_ARG, FilteredTasksFragment.INFINITE);
                    break;
                case FilteredTasksFragment.SIMPLE:
                    b.putInt(FilteredTasksFragment.FILTER_ARG, FilteredTasksFragment.SIMPLE);
                    break;
                case FilteredTasksFragment.DONE:
                    b.putInt(FilteredTasksFragment.FILTER_ARG, FilteredTasksFragment.DONE);
                    break;
                default:
            }
            Fragment f = new FilteredTasksFragment();
            f.setArguments(b);
            return f;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DefaultFragment f = (DefaultFragment) super.instantiateItem(container, position);
            fragments.put(position, f);
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            DefaultFragment f = (DefaultFragment) object;
            fragments.remove(f);
            super.destroyItem(container, position, object);

        }

        public Map<Integer, DefaultFragment> getFragments() {
            return fragments;
        }
    }
}
