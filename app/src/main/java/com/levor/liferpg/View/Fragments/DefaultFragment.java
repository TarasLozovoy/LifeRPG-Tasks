package com.levor.liferpg.View.Fragments;

import android.app.Fragment;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.View.Activities.MainActivity;

/**
 * Created by dtv on 11/2/15.
 */
public class DefaultFragment  extends Fragment{
    protected MainActivity getCurrentActivity(){
        return (MainActivity) getActivity();
    }

    protected LifeController getController(){
        return getCurrentActivity().getController();
    }
}
