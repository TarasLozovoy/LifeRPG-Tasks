package com.levor.liferpgtasks.view.fragments.rewards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.CustomPagerAdapter;
import com.levor.liferpgtasks.controller.RewardsController;
import com.levor.liferpgtasks.model.Reward;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.tasks.AddTaskFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardsFragment extends DefaultFragment {
    public static final String REWARDS_SORTING_KEY = "rewards_sorting_key";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    private int sorting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_with_tabs, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.new_rewards));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.claimed_rewards));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        sorting = getController().getSharedPreferences().getInt(REWARDS_SORTING_KEY, Reward.SortingOrder.TITLE_DESC);

        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_rewards, menu);
    }


    @Override
    public void onStart() {
        super.onStart();
        createViewPager();
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }
        setupFab();
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Reward Fragment");
    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(getResources().getDimension(R.dimen.standard_elevation));
        }
    }

    public void setupFab() {
        fab.show();
        if (viewPager.getCurrentItem() == FilteredRewardsFragment.NEW) {
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_black_24dp));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCurrentActivity().showChildFragment(new EditRewardFragment(), null);
                }
            });
        } else if (viewPager.getCurrentItem() == FilteredRewardsFragment.CLAIMED){
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_delete_black_24dp));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle(R.string.delete_all_claimed_rewards)
                            .setMessage(R.string.delete_all_claimed_rewards_message)
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RewardsController rewardsController = RewardsController.getInstance(getCurrentActivity());
                                    List<Reward> rewards = rewardsController.getAllRewards();
                                    for (Reward r : rewards) {
                                        if (r.isDone()) {
                                            rewardsController.removeReward(r);
                                        }
                                    }
                                    updateUI();
                                }
                            })
                            .show();
                }
            });
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
        getController().getSharedPreferences().edit().putInt(REWARDS_SORTING_KEY, sorting).apply();
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
                case FilteredRewardsFragment.NEW:
                    b.putInt(FilteredRewardsFragment.FILTER_ARG, FilteredRewardsFragment.NEW);
                    break;
                case FilteredRewardsFragment.CLAIMED:
                    b.putInt(FilteredRewardsFragment.FILTER_ARG, FilteredRewardsFragment.CLAIMED);
                    break;
                default:
            }
            Fragment f = new FilteredRewardsFragment();
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
