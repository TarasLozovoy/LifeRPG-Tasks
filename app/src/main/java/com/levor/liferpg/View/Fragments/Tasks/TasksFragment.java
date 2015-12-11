package com.levor.liferpg.View.Fragments.Tasks;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.levor.liferpg.Adapters.CustomPagerAdapter;
import com.levor.liferpg.R;
import com.levor.liferpg.SwipeOutViewPager;
import com.levor.liferpg.View.Fragments.Characteristics.CharacteristicsFragment;
import com.levor.liferpg.View.Fragments.DefaultFragment;
import com.levor.liferpg.View.Fragments.Hero.HeroFragment;
import com.levor.liferpg.View.Fragments.Skills.SkillsFragment;

public class TasksFragment extends DefaultFragment {
    private SwipeOutViewPager viewPager;
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

        viewPager = (SwipeOutViewPager) view.findViewById(R.id.pager);
        viewPager.setOnSwipeOutListener(getCurrentActivity());
        getCurrentActivity().getSupportActionBar().setElevation(0);
        getCurrentActivity().setActionBarTitle(R.string.tasks);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_task:
                getCurrentActivity().showChildFragment(new AddTaskFragment(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        createViewPager();
    }

    private void createViewPager(){
        PagerAdapter adapter = new PagerAdapter
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

    public class PagerAdapter extends CustomPagerAdapter {

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
    }
}
