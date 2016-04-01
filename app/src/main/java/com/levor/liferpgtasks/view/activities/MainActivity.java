package com.levor.liferpgtasks.view.activities;

import android.app.PendingIntent;
import android.app.Service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.model.Misc;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.view.fragments.DataDependantFrament;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.MainFragment;
import com.levor.liferpgtasks.view.fragments.settings.SettingsFragment;
import com.levor.liferpgtasks.view.fragments.tasks.DetailedTaskFragment;
import com.levor.liferpgtasks.view.fragments.tasks.TasksFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends BackUpActivity{
    public final static int MAIN_FRAGMENT_ID = 0;
    public final static int TASKS_FRAGMENT_ID = 1;
    public final static int SETTINGS_FRAGMENT_ID = 2;
    private static final String SELECTED_FRAGMENT_TAG = "selected_fragment_tag";
    private static final String PREMIUM_BOUGHT_TAG = "premium_bought_tag";

    InterstitialAd performTaskAd;
    InterstitialAd charsChartAd;
    InterstitialAd tasksChartAd;
    private TabLayout navigationTabLayout;
    private TabLayout.Tab heroNavigationTab;
    private static Stack<DefaultFragment> mainFragmentsStack = new Stack<>();
    private static Stack<DefaultFragment> tasksFragmentsStack = new Stack<>();
    private static Stack<DefaultFragment> settingsFragmentsStack = new Stack<>();
    private int currentFragmentID;

    private long appClosingTime;
    private IInAppBillingService mService;
    private Map<String, String> availablePurchases = new HashMap<>();
    private boolean premium = false;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifeController.setCurrentActivity(this);

        //setting up Google Analytics
        lifeController.setGATracker(getCurrentApplication().getDefaultTracker());

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        navigationTabLayout = (TabLayout) findViewById(R.id.navigation_tab_layout);
        setupNavigationTabs();

        navigationTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        navigationTabLayout.setSelectedTabIndicatorHeight(0);
        navigationTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showRootFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        lifeController.setupTasksNotifications();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (savedInstanceState == null) {
            DefaultFragment fragment = new MainFragment();
            currentFragmentID = MAIN_FRAGMENT_ID;
            mainFragmentsStack = new Stack<>();
            mainFragmentsStack.push(fragment);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
            fm.executePendingTransactions();
        } else {
            currentFragmentID = savedInstanceState.getInt(SELECTED_FRAGMENT_TAG);
            TabLayout.Tab tab = navigationTabLayout.getTabAt(currentFragmentID);
            if (tab != null) {
                tab.select();
            }
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String taskFromNotification = extras.getString(LifeController.TASK_TITLE_NOTIFICATION_TAG);
            if (taskFromNotification != null) {
                tasksFragmentsStack.clear();
                switchToRootFragment(TASKS_FRAGMENT_ID);

                Bundle b = new Bundle();
                Task t =  lifeController.getTaskByTitle(taskFromNotification);
                if (t != null) {
                    b.putSerializable(DetailedTaskFragment.SELECTED_TASK_UUID_TAG, t.getId());
                    showChildFragment(new DetailedTaskFragment(), b);
                }
            }
        }
        bindInAppPurchasesService();
        queryAvailablePurchases();
        setupInterstitialAds();
        lifeController.checkTasksPerDay();
        lifeController.checkHabitGenerationForAllTasks();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getCurrentFragmentsStack().isEmpty() ||
                getCurrentFragment().onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case android.R.id.home:
                showPreviousFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lifeController.isFirstRun()){
            showCoachmarks();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        lifeController.updateMiscToDB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_FRAGMENT_TAG, currentFragmentID);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    public LifeController getController(){
        return lifeController;
    }

    public Stack<DefaultFragment> getCurrentFragmentsStack(){
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

    public DefaultFragment getCurrentFragment() {
        return getCurrentFragmentsStack().peek();
    }

    private void showRootFragment(int fragmentID) {
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
        if (fragment instanceof DataDependantFrament &&
                !((DataDependantFrament)fragment).isDependableDataAvailable()){
            if (fragmentID == MAIN_FRAGMENT_ID) mainFragmentsStack.pop();
            if (fragmentID == TASKS_FRAGMENT_ID) tasksFragmentsStack.pop();
            if (fragmentID == SETTINGS_FRAGMENT_ID) settingsFragmentsStack.pop();
            showRootFragment(fragmentID);
            return;
        }
        if (getCurrentFragmentsStack().isEmpty()) return;
        currentFragmentID = fragmentID;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public void showPreviousFragment() {
        if (getCurrentFragmentsStack().isEmpty()
                || getCurrentFragmentsStack().size() == 1) return;
        getCurrentFragmentsStack().pop();
        DefaultFragment fragment;
        try {
            fragment = getCurrentFragment();
        } catch (EmptyStackException e){
            return;
        }
        if (fragment instanceof DataDependantFrament &&
                !((DataDependantFrament)fragment).isDependableDataAvailable()){
            showPreviousFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.exit_right)
                .replace(R.id.content_frame, fragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void showNthPreviousFragment(int n) {
        if (n < 2 || getCurrentFragmentsStack().size() <= 2) {
            showPreviousFragment();
            return;
        }
        getCurrentFragmentsStack().pop();
        showNthPreviousFragment(n - 1);
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
        TabLayout.Tab tab = navigationTabLayout.getTabAt(id);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment().onBackPressed()) return;
        if (getCurrentFragmentsStack().size() == 1) {
            if (System.currentTimeMillis() - appClosingTime > 2500){
                appClosingTime = System.currentTimeMillis();
                Toast.makeText(this, getString(R.string.closing_app_toast), Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        } else if (getCurrentFragmentsStack().size() > 1){
            showPreviousFragment();
        } else { //something went wrong and fragmentStack size < 1
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        if (isBack) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(0);
        } else {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    public void showSoftKeyboard(boolean show, View rootView){
        InputMethodManager imm = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
        if (show){
            imm.showSoftInput(rootView, 0);
        } else {
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
    }

    public void setHeroImageName(String name){
        if (name != null) {
            Misc.HERO_IMAGE_PATH = name;
            setupNavigationTabs();
        }
    }

    public Bitmap getHeroIconBitmap(){
        try {
            InputStream is = getAssets().open(Misc.HERO_IMAGE_PATH);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setupNavigationTabs(){
        Drawable d;
        try {
            InputStream is = getAssets().open(Misc.HERO_IMAGE_PATH);
            d = Drawable.createFromStream(is, null);
        } catch (IOException e) {
            Misc.HERO_IMAGE_PATH = "elegant5.png";
            Toast.makeText(this, R.string.error_on_loading_image, Toast.LENGTH_LONG).show();
            setupNavigationTabs();
            return;
        }
        navigationTabLayout.removeAllTabs();
        heroNavigationTab = navigationTabLayout.newTab().setIcon(d);
        navigationTabLayout.addTab(heroNavigationTab);
        navigationTabLayout.addTab(navigationTabLayout.newTab().setText(R.string.tasks));
        navigationTabLayout.addTab(navigationTabLayout.newTab().setText(R.string.settings));
    }

    private void updateHeroNavigationTab(){
        try {
            InputStream is = getAssets().open(Misc.HERO_IMAGE_PATH);
            Drawable d = Drawable.createFromStream(is, null);
            heroNavigationTab.setIcon(d);
        } catch (IOException e) {}
    }

    @Override
    public void onDBFileUpdated(boolean isFileDeleted){
        getController().onDBFileUpdated(isFileDeleted);
        updateHeroNavigationTab();
        mainFragmentsStack = new Stack<>();
        tasksFragmentsStack = new Stack<>();
        mainFragmentsStack.add(new MainFragment());
        tasksFragmentsStack.add(new TasksFragment());
    }

    public void showCoachmarks(){
        final View bottomCoachmarks = findViewById(R.id.bottom_coachmarks);
        final View xpCoachmarks = findViewById(R.id.xp_coachmarks);
        final View coachmarksDim = findViewById(R.id.coachmarks_dim);
        coachmarksDim.setVisibility(View.VISIBLE);
        coachmarksDim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.hero_coachmarks).setVisibility(View.GONE);
                xpCoachmarks.setVisibility(View.VISIBLE);
                lifeController.setFirstRun(false);
                coachmarksDim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xpCoachmarks.setVisibility(View.GONE);
                        bottomCoachmarks.setVisibility(View.VISIBLE);
                        coachmarksDim.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                coachmarksDim.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });
    }

    private void setupInterstitialAds() {
        if(premium) return;
        performTaskAd = new InterstitialAd(this);
        performTaskAd.setAdUnitId(getString(R.string.interstitial_perform_task_banner_ad_unit_id));
        performTaskAd.setAdListener(new CustomAdListener());
        requestNewInterstitial(performTaskAd);

        charsChartAd = new InterstitialAd(this);
        charsChartAd.setAdUnitId(getString(R.string.interstitial_chars_chart_banner_ad_unit_id));
        charsChartAd.setAdListener(new CustomAdListener());
        requestNewInterstitial(charsChartAd);

        tasksChartAd = new InterstitialAd(this);
        tasksChartAd.setAdUnitId(getString(R.string.interstitial_tasks_per_day_chart_banner_ad_unit_id));
        tasksChartAd.setAdListener(new CustomAdListener());
        requestNewInterstitial(tasksChartAd);
    }

    private void requestNewInterstitial(InterstitialAd ad) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("9DA2C80CC6BDB238BAD014DE697F3902")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        ad.loadAd(adRequest);
    }

    public void showInterstitialAd(AdType type){
        if(premium) return;
        InterstitialAd ad = null;
        int successShowRate = 0;
        switch (type) {
            case PERFORM_TASK:
                successShowRate = 35;
                ad = performTaskAd;
                break;
            case CHARACTERISTICS_CHART:
                successShowRate = 50;
                ad = charsChartAd;
                break;
            case TASKS_PER_DAY_CHART:
                successShowRate = 50;
                ad = tasksChartAd;
                break;
        }
        if (ad == null) return;
        if (ad.isLoaded() && new Random().nextInt(100) < successShowRate) {
            ad.show();
        } else if ((ad.isLoading() || !ad.isLoaded())
                && lifeController.isInternetConnectionActive()){
            requestNewInterstitial(ad);
        }
    }

    private void bindInAppPurchasesService() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private void queryAvailablePurchases() {
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add(getString(R.string.purchase_premium));
        skuList.add(getString(R.string.purchase_1_donation));
        skuList.add(getString(R.string.purchase_2_donation));
        skuList.add(getString(R.string.purchase_3_donation));
        skuList.add(getString(R.string.purchase_5_donation));
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle querySkus = params[0];
                try {
                    if (mService == null) {
                        return null;
                    }
                    return mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bundle skuDetails) {
                if (skuDetails == null) {
                    queryAvailablePurchases();
                    return;
                }
                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                    for (String thisResponse : responseList) {
                        try {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");
                            availablePurchases.put(sku, price);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!availablePurchases.containsKey(getString(R.string.purchase_premium))) {
                        premium = true;
                        switchPremiumMode(premium);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, querySkus);
    }

    private void checkForPremium() {
        premium = getController().getSharedPreferences().getBoolean(PREMIUM_BOUGHT_TAG, false);
        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                if (ownedSkus != null) {
                    if (ownedSkus.contains(getString(R.string.purchase_premium))) {
                        switchPremiumMode(true);
                    } else {
                        switchPremiumMode(false);
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void switchPremiumMode(boolean isPremium) {
        if (premium != isPremium) {
            premium = isPremium;
            getController().getSharedPreferences().edit().putBoolean(PREMIUM_BOUGHT_TAG, premium).apply();
            if(!isPremium) {
                setupInterstitialAds();
            }
        }
    }

    public boolean isPremium() {
        return premium;
    }

    public String getPremiumPrice() {
        String price = availablePurchases.get(getString(R.string.purchase_premium));
        if (price == null || price.isEmpty()) {
            return "$2";
        } else {
            return price;
        }
    }

    public String getPurchasePrice(String itemKey, String defaultValue) {
        String price = availablePurchases.get(itemKey);
        if (price == null || price.isEmpty()) {
            return defaultValue;
        } else {
            return price;
        }
    }

    public boolean performBuy(String itemKey, String developerPayload) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    itemKey, "inapp", developerPayload);
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            startIntentSenderForResult(pendingIntent.getIntentSender(),
                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0));
            return true;
        } catch (RemoteException |IntentSender.SendIntentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int consumePurchase(String token) {
        try {
            int response = mService.consumePurchase(3, getPackageName(), token);
            return response;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public enum AdType { PERFORM_TASK, CHARACTERISTICS_CHART, TASKS_PER_DAY_CHART}

    private class CustomAdListener extends AdListener {
        @Override
        public void onAdClosed() {
            requestNewInterstitial(tasksChartAd);
            lifeController.getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.GA_action))
                    .setAction(getString(R.string.GA_ad_shown))
                    .setValue(1)
                    .build());
        }
    }

}
