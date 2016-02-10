package com.levor.liferpgtasks.view.fragments.tasks;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.levor.liferpgtasks.adapters.CustomPagerAdapter;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.HashSet;
import java.util.Set;

public class TasksFragment extends DefaultFragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;

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

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }
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
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    public void updateChildFragmentsUI(){
        PagerAdapter adapter = (PagerAdapter) viewPager.getAdapter();
        for (DefaultFragment f : adapter.getFragments()) {
            if (f != null && f.isCreated()){
                f.updateUI();
            }
        }
    }

    public class PagerAdapter extends CustomPagerAdapter {

        private Set<DefaultFragment> fragments = new HashSet<>();

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
            fragments.add(f);
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            DefaultFragment f = (DefaultFragment) object;
            fragments.remove(f);
            super.destroyItem(container, position, object);

        }

        public Set<DefaultFragment> getFragments() {
            return fragments;
        }
    }
}
