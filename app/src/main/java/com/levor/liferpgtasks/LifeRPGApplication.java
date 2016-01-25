package com.levor.liferpgtasks;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.levor.liferpgtasks.controller.LifeController;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;

public class LifeRPGApplication extends Application {
    private Tracker mTracker;
    private DropboxAPI<AndroidAuthSession> DBApi;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeSocialNetworksSDK();
        initializeDBBackupServices();
    }

    private void initializeSocialNetworksSDK(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        VKSdk.initialize(getApplicationContext());

        TwitterAuthConfig authConfig =  new TwitterAuthConfig("w4GMxIA6anN7qWmqgyGFGGd2r", "skRyGWmGKYzJBWQrflywymbH6NYpdxHtV2JijiRKRyILANrQLp");
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer(), new Crashlytics());
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    private void initializeDBBackupServices(){
        String dropboxKey = getString(R.string.dropbox_app_key);
        String dropboxSecret = getString(R.string.dropbox_app_secret);
        AppKeyPair appKeys = new AppKeyPair(dropboxKey, dropboxSecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        SharedPreferences prefs = getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        boolean backupEnabled = prefs.getBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false);
        if (backupEnabled){
            String accessToken = prefs.getString(LifeController.DROPBOX_ACCESS_TOKEN_TAG, null);
            if (accessToken != null){
                session.setOAuth2AccessToken(accessToken);
            }
        }
        DBApi = new DropboxAPI<>(session);
    }

    public DropboxAPI<AndroidAuthSession> getDBApi() {
        return DBApi;
    }
}
