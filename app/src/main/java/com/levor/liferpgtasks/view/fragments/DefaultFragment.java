package com.levor.liferpgtasks.view.fragments;



import android.app.Activity;
import android.support.v4.app.Fragment;

import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.view.activities.MainActivity;

public  class DefaultFragment extends Fragment {
    private Activity activity;
    protected boolean isCreated = false;

    protected MainActivity getCurrentActivity(){
        return (MainActivity) activity;
    }

    protected LifeController getController(){
        return getCurrentActivity().getController();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCreated = false;
    }

    public void updateUI(){}

    public boolean onBackPressed(){
        return false;
    }

    public boolean isCreated(){
        return isCreated;
    }
}
