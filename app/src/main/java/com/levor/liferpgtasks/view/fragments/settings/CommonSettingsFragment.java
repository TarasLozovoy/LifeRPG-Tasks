package com.levor.liferpgtasks.view.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Levor on 19.07.2016.
 */
public class CommonSettingsFragment extends DefaultFragment {
    @Bind(R.id.disable_sounds_layout) View disableSoundsView;
    @Bind(R.id.disable_sounds_switch) Switch disableSoundsSwitch;
    @Bind(R.id.only_today_tasks_layout) View onlyTodayTasksView;
    @Bind(R.id.only_today_tasks_switch) Switch onlyTodayTasksSwitch;
    @Bind(R.id.dailies_in_done_layout)  View dailiesInDoneView;
    @Bind(R.id.dailies_in_done_switch)  Switch dailiesInDoneSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_common_settings, container, false);
        ButterKnife.bind(this, v);

        disableSoundsSwitch.setChecked(getController().getSharedPreferences().getBoolean(LifeController.DISABLE_SOUNDS_TAG, false));
        onlyTodayTasksSwitch.setChecked(getController().getSharedPreferences().getBoolean(LifeController.SHOW_ONLY_TODAY_TASK_TAG, false));
        dailiesInDoneSwitch.setChecked(getController().getSharedPreferences().getBoolean(LifeController.SHOW_DAILIES_IN_DONE_TAG, false));

        disableSoundsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableSoundsSwitch.setChecked(!disableSoundsSwitch.isChecked());
            }
        });

        disableSoundsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getController().getSharedPreferences().edit().putBoolean(LifeController.DISABLE_SOUNDS_TAG, isChecked).apply();
            }
        });

        onlyTodayTasksView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlyTodayTasksSwitch.setChecked(!onlyTodayTasksSwitch.isChecked());
            }
        });

        onlyTodayTasksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getController().getSharedPreferences().edit().putBoolean(LifeController.SHOW_ONLY_TODAY_TASK_TAG, isChecked).apply();
            }
        });

        dailiesInDoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailiesInDoneSwitch.setChecked(!dailiesInDoneSwitch.isChecked());
            }
        });

        dailiesInDoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getController().getSharedPreferences().edit().putBoolean(LifeController.SHOW_DAILIES_IN_DONE_TAG, isChecked).apply();
            }
        });

        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.common_settings));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Common Settings Fragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}