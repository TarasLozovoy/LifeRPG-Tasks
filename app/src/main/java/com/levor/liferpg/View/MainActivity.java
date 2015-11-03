package com.levor.liferpg.View;

import android.app.Fragment;

import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.CharacteristicsFragment;
import com.levor.liferpg.View.Fragments.SkillsFragment;
import com.levor.liferpg.View.Fragments.TasksFragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EmptyStackException;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private final String SKILLS_FILE_NAME = "skills_file_name.txt";
    private final String CHARACTERISTICS_FILE_NAME = "characteristics_file_name.txt";
    private final String TASKS_FILE_NAME = "tasks_file_name.txt";
    private final String TAG = "com.levor.liferpg";

    private String skillsFromFile;
    private String characteristicsFromFile;
    private String tasksFromFile;

    private final LifeController lifeController = LifeController.getInstance();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] activities;
    private Stack<Fragment> fragmentsStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        activities = getResources().getStringArray(R.array.activities_array);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activities));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        readContentStringsFromFiles();

        Fragment fragment = new TasksFragment();
        fragmentsStack.push(fragment);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .add(R.id.content_frame, fragment)
                .commit();

    }

    @Override
    protected void onPause() {
        writeContentStringsToFile();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readContentStringsFromFiles(){
        characteristicsFromFile = getStringFromFile(CHARACTERISTICS_FILE_NAME);
        skillsFromFile = getStringFromFile(SKILLS_FILE_NAME);
        tasksFromFile = getStringFromFile(TASKS_FILE_NAME);
        Log.e(TAG, "chars: " + characteristicsFromFile + "\nskiils: " + skillsFromFile + "\nTasks: " + tasksFromFile);
        lifeController.updateCurrentContentWithStrings(characteristicsFromFile, skillsFromFile, tasksFromFile);
    }

    private String getStringFromFile(String fileName){
        try{
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            fis.close();
            return sb.toString();
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return "";
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public LifeController getController(){
        return lifeController;
    }

    private void writeContentStringsToFile(){
        writeStringToFile(lifeController.getCurrentCharacteristicsString(), CHARACTERISTICS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentSkillsString(), SKILLS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentTasksString(), TASKS_FILE_NAME);
        Log.d(TAG, "content saved to filesystem");
    }

    private void writeStringToFile(String str, String fileName){
        try{
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveAppDataToFile(){
        writeContentStringsToFile();
    }

    private void switchRootFragment(int fragmentNumber) {
        Fragment fragment = null;
        switch (fragmentNumber) {
            case 0 :
                fragment = new TasksFragment();
                break;
            case 1:
                fragment = new SkillsFragment();
                break;
            case 2:
                fragment = new CharacteristicsFragment();
                break;
            default:
                throw new RuntimeException("No such menu item!");
        }
        showRootFragment(fragment, null);
        mDrawerList.setItemChecked(fragmentNumber, true);
        mDrawerLayout.closeDrawer(mDrawerList);
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
        return true;
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            switchRootFragment(position);
        }
    }
}
