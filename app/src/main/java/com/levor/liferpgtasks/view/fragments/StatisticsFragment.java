package com.levor.liferpgtasks.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;

public class StatisticsFragment extends DefaultFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        TextView statisticsTV = (TextView) v.findViewById(R.id.statistics_text_view);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.performed_tasks_number))
                .append(" ")
                .append((int)getController().getStatisticsValue(LifeController.PERFORMED_TASKS_TAG))
                .append("\n")
                .append(getString(R.string.added_tasks_number))
                .append(" ")
                .append((int)getController().getStatisticsValue(LifeController.TOTAL_TASKS_NUMBER_TAG))
                .append("\n")
                .append(getString(R.string.total_hero_xp))
                .append(" ")
                .append(getController().getStatisticsValue(LifeController.TOTAL_HERO_XP_TAG))
                .append("\n")
                .append(getString(R.string.total_skills_xp))
                .append(" ")
                .append(getController().getStatisticsValue(LifeController.TOTAL_SKILLS_XP_TAG))
                .append("\n")
                .append(getString(R.string.xp_multiplier))
                .append(" ")
                .append(getController().getStatisticsValue(LifeController.XP_MULTIPLIER_TAG))
                .append("\n");
        statisticsTV.setText(sb.toString());

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.statistics));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }
}
