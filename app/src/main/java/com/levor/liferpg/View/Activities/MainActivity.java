package com.levor.liferpg.View.Activities;

import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;
import com.levor.liferpg.SwipeOutViewPager;
import com.levor.liferpg.View.Fragments.DefaultFragment;
import com.levor.liferpg.View.Fragments.MainFragment;
import com.levor.liferpg.View.Fragments.SettingsFragment;
import com.levor.liferpg.View.Fragments.Tasks.TasksFragment;

import java.util.EmptyStackException;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements SwipeOutViewPager.OnSwipeOutListener{
    public final static int HERO_FRAGMENT_ID = 0;
    public final static int TASKS_FRAGMENT_ID = 1;
    public final static int SETTINGS_FRAGMENT_ID = 2;
    protected final String TAG = "com.levor.liferpg";

    protected LifeController lifeController;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private String[] activities;
    private static Stack<DefaultFragment> fragmentsStack = new Stack<>();
    private boolean showBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifeController = LifeController.getInstance(this);
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
                getSupportActionBar().setHomeAsUpIndicator(0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setHomeAsUpIndicator(showBack ? 0 : R.drawable.ic_menu_black_24dp);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);


        if (savedInstanceState == null) {
            DefaultFragment fragment = new MainFragment();
            fragmentsStack.push(fragment);
            FragmentManager fm = getSupportFragmentManager();
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
        } else {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        return super.onOptionsItemSelected(item);
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
        DefaultFragment fragment;
        switch (fragmentNumber) {
            case HERO_FRAGMENT_ID :
                fragment = new MainFragment();
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
        DefaultFragment fragment;
        try {
            fragment = fragmentsStack.peek();
        } catch (EmptyStackException e){
            return false;
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.exit_right)
                .replace(R.id.content_frame, fragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        fragment.onRestoreFromBackStack();
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

    public void showChildFragment(DefaultFragment fragment, Bundle bundle){
        fragment.setArguments(bundle);
        fragmentsStack.push(fragment);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right, R.anim.exit_left)
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public void showRootFragment(DefaultFragment fragment, Bundle bundle){
        fragment.setArguments(bundle);
        fragmentsStack.clear();
        fragmentsStack.push(fragment);
        getSupportFragmentManager().beginTransaction()
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
    public void onSwipeOutAtStart() {
        mDrawerLayout.openDrawer(mDrawerList);
    }

    @Override
    public void onSwipeOutAtEnd() {
        //Do nothing
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            switchRootFragment(position);
        }
    }
}
