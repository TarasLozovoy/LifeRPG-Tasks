package com.levor.liferpgtasks.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.CustomPagerAdapter;
import com.levor.liferpgtasks.view.fragments.characteristics.CharacteristicsFragment;
import com.levor.liferpgtasks.view.fragments.characteristics.EditCharacteristicFragment;
import com.levor.liferpgtasks.view.fragments.hero.EditHeroFragment;
import com.levor.liferpgtasks.view.fragments.hero.HeroFragment;
import com.levor.liferpgtasks.view.fragments.skills.AddSkillFragment;
import com.levor.liferpgtasks.view.fragments.skills.SkillsFragment;

public class MainFragment extends DefaultFragment{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager_with_tabs, container, false);

        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        TabLayout.Tab heroTab = tabLayout.newTab().setText(R.string.hero_fragment_name);
        TabLayout.Tab characteristicsTab = tabLayout.newTab().setText(R.string.characteristics_fragment_name);
        TabLayout.Tab skillsTab = tabLayout.newTab().setText(R.string.skills_fragment_name);

        if (getController().getScreenWidth() < 750) {
            View customView = LayoutInflater.from(getCurrentActivity()).inflate(R.layout.tab_in_fragment_layout, null);
            TextView tv = (TextView) customView.findViewById(R.id.custom_text);
            tv.setText(R.string.characteristics_fragment_name);
            characteristicsTab.setCustomView(customView);
        }

        tabLayout.addTab(heroTab);
        tabLayout.addTab(characteristicsTab);
        tabLayout.addTab(skillsTab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) v.findViewById(R.id.pager);

        getCurrentActivity().setActionBarTitle(R.string.app_name);
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        createViewPager();
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setElevation(0);
        }
        setupFab(viewPager.getCurrentItem());
    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBar actionBar = getCurrentActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setElevation(getResources().getDimension(R.dimen.standard_elevation));
        }
    }

    public void setupFab(int selectedFragment) {
        fab.show();
        switch (selectedFragment) {
            case 0:
                fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_mode_edit_black_24dp));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCurrentActivity().showChildFragment(new EditHeroFragment(), null);
                    }
                });
                break;
            case 1:
                fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_black_24dp));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCurrentActivity().showChildFragment(new EditCharacteristicFragment(), null);
                    }
                });
                break;
            case 2:
                fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_black_24dp));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCurrentActivity().showChildFragment(new AddSkillFragment(), null);
                    }
                });
                break;
        }
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
                setupFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setupFab(tab.getPosition());
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
