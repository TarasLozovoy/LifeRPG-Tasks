package com.levor.liferpg.view.fragments;



import android.support.v4.app.Fragment;

import com.levor.liferpg.controller.LifeController;
import com.levor.liferpg.view.activities.MainActivity;

public  class DefaultFragment  extends Fragment {
    protected MainActivity getCurrentActivity(){
        return (MainActivity) getActivity();
    }

    protected LifeController getController(){
        return getCurrentActivity().getController();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    protected void updateUI(){}

    public void onRestoreFromBackStack(){}
}
