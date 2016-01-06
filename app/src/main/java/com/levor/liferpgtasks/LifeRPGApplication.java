package com.levor.liferpgtasks;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;

public class LifeRPGApplication extends Application {
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeSocialNetworksSDK();
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
}
