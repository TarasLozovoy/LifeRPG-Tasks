package com.levor.liferpg.View.Fragments;



import android.support.v4.app.Fragment;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.View.Activities.MainActivity;

public class DefaultFragment  extends Fragment {
    protected MainActivity getCurrentActivity(){
        return (MainActivity) getActivity();
    }

    protected LifeController getController(){
        return getCurrentActivity().getController();
    }
}
