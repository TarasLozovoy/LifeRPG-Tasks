package com.levor.liferpgtasks;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.vk.sdk.VKSdk;

public class LifeRPGApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeSocialNetworksSDK();
    }

    private void initializeSocialNetworksSDK(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        VKSdk.initialize(getApplicationContext());
    }
}
