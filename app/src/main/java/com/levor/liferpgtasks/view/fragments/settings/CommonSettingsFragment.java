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
    @Bind(R.id.only_today_tasks_layout) View onlyTodayTasksView;
    @Bind(R.id.only_today_tasks_switch) Switch onlyTodayTasksSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_common_settings, container, false);
        ButterKnife.bind(this, v);

        onlyTodayTasksSwitch.setChecked(getController().getSharedPreferences().getBoolean(LifeController.SHOW_ONLY_TODAY_TASK_TAG, false));

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

        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.about));
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