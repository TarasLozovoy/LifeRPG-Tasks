package com.levor.liferpgtasks.view.fragments;



import android.support.v4.app.Fragment;

import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.view.activities.MainActivity;

public  class DefaultFragment extends Fragment {
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
