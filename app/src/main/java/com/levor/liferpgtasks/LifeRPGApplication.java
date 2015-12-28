package com.levor.liferpgtasks;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;

public class LifeRPGApplication extends Application {

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
}
