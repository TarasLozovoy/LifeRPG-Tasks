package com.levor.liferpg.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.levor.liferpg.Adapters.CustomPagerAdapter;
import com.levor.liferpg.R;
import com.levor.liferpg.SwipeOutViewPager;
import com.levor.liferpg.View.Fragments.Characteristics.CharacteristicsFragment;
import com.levor.liferpg.View.Fragments.Hero.HeroFragment;
import com.levor.liferpg.View.Fragments.Skills.SkillsFragment;

public class MainFragment extends DefaultFragment{
    private SwipeOutViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager_with_tabs, container, false);

        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.hero_fragment_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.characteristics_fragment_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.skills_fragment_name));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (SwipeOutViewPager) v.findViewById(R.id.pager);
        viewPager.setOnSwipeOutListener(getCurrentActivity());
        getCurrentActivity().getSupportActionBar().setElevation(0);
        getCurrentActivity().setActionBarTitle(R.string.real_liferpg);
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        return v;
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
            public void onTabUnselected(TabLayout.Tab tab) {}

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
            switch (position) {
                case 0:
                    return new HeroFragment();
                case 1:
                    return new CharacteristicsFragment();
                case 2:
                    return new SkillsFragment();
                default:
                    return null;
            }
        }
    }
}
