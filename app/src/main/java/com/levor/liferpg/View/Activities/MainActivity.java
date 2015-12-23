package com.levor.liferpg.View.Activities;

import android.app.Service;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.FacebookSdk;
import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;
import com.levor.liferpg.View.Fragments.MainFragment;
import com.levor.liferpg.View.Fragments.SettingsFragment;
import com.levor.liferpg.View.Fragments.Tasks.TasksFragment;

import java.util.EmptyStackException;
import java.util.Stack;

import bolts.AppLinks;

public class MainActivity extends AppCompatActivity{
    public final static int MAIN_FRAGMENT_ID = 0;
    public final static int TASKS_FRAGMENT_ID = 1;
    public final static int SETTINGS_FRAGMENT_ID = 2;
    protected final String TAG = "com.levor.liferpg";

    protected LifeController lifeController;

    private TabLayout navigationTabLayout;
    private static Stack<DefaultFragment> mainFragmentsStack = new Stack<>();
    private static Stack<DefaultFragment> tasksFragmentsStack = new Stack<>();
    private static Stack<DefaultFragment> settingsFragmentsStack = new Stack<>();

    private int currentFragmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSocialNetworksSDK();
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }
        lifeController = LifeController.getInstance(this);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        navigationTabLayout = (TabLayout) findViewById(R.id.navigation_tab_layout);
        navigationTabLayout.addTab(navigationTabLayout.newTab().setIcon(getHeroImageID()));
        navigationTabLayout.addTab(navigationTabLayout.newTab().setText(R.string.tasks));
        navigationTabLayout.addTab(navigationTabLayout.newTab().setText(R.string.settings));

        navigationTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        navigationTabLayout.setSelectedTabIndicatorHeight(6);
        navigationTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue));
        navigationTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchRootFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (savedInstanceState == null) {
            DefaultFragment fragment = new MainFragment();
            currentFragmentID = MAIN_FRAGMENT_ID;
            mainFragmentsStack.push(fragment);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getCurrentFragmentsStack().peek().onOptionsItemSelected(item)) return true;
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

    private Stack<DefaultFragment> getCurrentFragmentsStack(){
        switch (currentFragmentID){
            case MAIN_FRAGMENT_ID :
                return mainFragmentsStack;
            case TASKS_FRAGMENT_ID :
                return tasksFragmentsStack;
            case SETTINGS_FRAGMENT_ID :
                return settingsFragmentsStack;
            default:
                throw new RuntimeException("Unexpected fragment ID");
        }
    }

    public int getHeroImageID(){
        return R.drawable.default_hero;
    }

    private void switchRootFragment(int fragmentID) {
        DefaultFragment fragment;
        switch (fragmentID) {
            case MAIN_FRAGMENT_ID:
                if (mainFragmentsStack.empty()){
                    fragment = new MainFragment();
                    mainFragmentsStack.push(fragment);
                } else {
                    fragment = mainFragmentsStack.peek();
                }
                break;
            case TASKS_FRAGMENT_ID :
                if (tasksFragmentsStack.empty()){
                    fragment = new TasksFragment();
                    tasksFragmentsStack.push(fragment);
                } else {
                    fragment = tasksFragmentsStack.peek();
                }
                break;
            case SETTINGS_FRAGMENT_ID:
                if (settingsFragmentsStack.empty()){
                    fragment = new SettingsFragment();
                    settingsFragmentsStack.push(fragment);
                } else {
                    fragment = settingsFragmentsStack.peek();
                }
                break;
            default:
                throw new RuntimeException("No such menu item!");
        }
        if (getCurrentFragmentsStack().peek().getClass() == fragment.getClass()) return;
        currentFragmentID = fragmentID;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public boolean showPreviousFragment() {
        getCurrentFragmentsStack().pop();
        DefaultFragment fragment;
        try {
            fragment = getCurrentFragmentsStack().peek();
        } catch (EmptyStackException e){
            return false;
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.exit_right)
                .replace(R.id.content_frame, fragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        fragment.onRestoreFromBackStack();
        return true;
    }

    public boolean showNthPreviousFragment(int n) {
        if (n <= 1 || getCurrentFragmentsStack().size() == 1) {
            return showPreviousFragment();
        }
        getCurrentFragmentsStack().pop();
        return showNthPreviousFragment(n - 1);
    }

    public void showChildFragment(DefaultFragment fragment, Bundle bundle){
        fragment.setArguments(bundle);
        getCurrentFragmentsStack().push(fragment);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right, R.anim.exit_left)
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public void switchToRootFragment(int id){
        navigationTabLayout.getTabAt(id).select();
    }

    @Override
    public void onBackPressed() {
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(0);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }

    private void initializeSocialNetworksSDK(){
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public void showSoftKeyboard(boolean show, View rootView){
        InputMethodManager imm = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
        if (show){
            imm.showSoftInput(rootView, 0);
        } else {
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
    }
}
