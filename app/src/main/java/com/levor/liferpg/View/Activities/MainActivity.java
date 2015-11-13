package com.levor.liferpg.View.Activities;

import android.app.Fragment;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.Characteristics.CharacteristicsFragment;
import com.levor.liferpg.View.Fragments.HeroMainFragment;
import com.levor.liferpg.View.Fragments.SettingsFragment;
import com.levor.liferpg.View.Fragments.Skills.SkillsFragment;
import com.levor.liferpg.View.Fragments.Tasks.TasksFragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EmptyStackException;
import java.util.Stack;

public class MainActivity extends SaverActivity {
    public final static int HERO_FRAGMENT_ID = 0;
    public final static int TASKS_FRAGMENT_ID = 1;
    public final static int SETTINGS_FRAGMENT_ID = 2;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private String[] activities;
    private static Stack<Fragment> fragmentsStack = new Stack<>();
    private boolean showBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        activities = getResources().getStringArray(R.array.activities_array);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activities));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.mipmap.ic_drawer, R.string.drawer_open, R.string.drawer_close){
            public void onDrawerOpened(View view) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_blue)));
                getSupportActionBar().setHomeAsUpIndicator(0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                getSupportActionBar().setHomeAsUpIndicator(showBack ? 0 : R.drawable.ic_menu_black_24dp);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);


        if (savedInstanceState == null) {
            Fragment fragment = new HeroMainFragment();
            fragmentsStack.push(fragment);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (fragmentsStack.peek().onOptionsItemSelected(item)) return true;
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        writeContentStringsToFile();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    public LifeController getController(){
        return lifeController;
    }

    private void switchRootFragment(int fragmentNumber) {
        Fragment fragment;
        switch (fragmentNumber) {
            case HERO_FRAGMENT_ID :
                fragment = new HeroMainFragment();
                break;
            case TASKS_FRAGMENT_ID :
                fragment = new TasksFragment();
                break;
            case SETTINGS_FRAGMENT_ID:
                fragment = new SettingsFragment();
                break;
            default:
                throw new RuntimeException("No such menu item!");
        }
        mDrawerList.setItemChecked(fragmentNumber, true);
        mDrawerLayout.closeDrawer(mDrawerList);
        if (fragmentsStack.peek().getClass() == fragment.getClass()) return;
        showRootFragment(fragment, null);
    }

    public boolean showPreviousFragment() {
        fragmentsStack.pop();
        Fragment fragment;
        try {
            fragment = fragmentsStack.peek();
        } catch (EmptyStackException e){
            return false;
        }
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.exit_right)
                .replace(R.id.content_frame, fragment)
                .commit();
        mDrawerLayout.closeDrawer(mDrawerList);
        return true;
    }

    public boolean showNthPreviousFragment(int n) {
        if (n <= 1 || fragmentsStack.size() == 1) {
            return showPreviousFragment();
        }
        fragmentsStack.pop();
        return showNthPreviousFragment(n - 1);
    }

    public void showChildFragment(Fragment fragment, Bundle bundle){
        fragment.setArguments(bundle);
        fragmentsStack.push(fragment);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right, R.anim.exit_left)
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public void showRootFragment(Fragment fragment, Bundle bundle){
        fragment.setArguments(bundle);
        fragmentsStack.clear();
        fragmentsStack.push(fragment);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.exit_right)
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        if (!showPreviousFragment()){
            super.onBackPressed();
        }
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) != null) {
            actionBar.setTitle(title);
        }
    }

    public void setActionBarTitle(int id) {
        String title = getResources().getString(id);
        setActionBarTitle(title);
    }

    public void showActionBarHomeButtonAsBack(boolean isBack) {
        if (isBack) {
            getSupportActionBar().setHomeAsUpIndicator(0);
            showBack = true;
        } else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            showBack = false;
        }
    }

    @Override
    protected Fragment getCurrentFragment() {
        return fragmentsStack.peek();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            switchRootFragment(position);
        }
    }
}
