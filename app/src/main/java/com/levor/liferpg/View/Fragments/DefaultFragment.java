package com.levor.liferpg.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.View.MainActivity;

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
