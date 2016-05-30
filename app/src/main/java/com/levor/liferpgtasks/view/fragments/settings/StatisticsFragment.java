package com.levor.liferpgtasks.view.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.characteristics.CharacteristicsChartFragment;
import com.levor.liferpgtasks.view.fragments.skills.SkillsChartFragment;
import com.levor.liferpgtasks.view.fragments.tasks.TasksPerDayChartFragment;

import org.joda.time.LocalDate;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StatisticsFragment extends DefaultFragment {
    @Bind(R.id.statistics_text_view)            TextView statisticsTV;
    @Bind(R.id.characteristics_chart_layout)    View charsChartView;
    @Bind(R.id.skills_chart_layout)             View skillsChartView;
    @Bind(R.id.tasks_per_day_chart_layout)      View tasksPerDayChartView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, v);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.performed_tasks_number))
                .append(" ")
                .append((int)getController().getStatisticsValue(LifeController.PERFORMED_TASKS_TAG))
                .append("\n")
                .append(getString(R.string.added_tasks_number))
                .append(" ")
                .append((int)getController().getStatisticsValue(LifeController.TOTAL_TASKS_NUMBER_TAG))
                .append("\n")
                .append(getString(R.string.finished_tasks_number))
                .append(" ")
                .append((int)getController().getStatisticsValue(LifeController.FINISHED_TASKS_NUMBER_TAG))
                .append("\n")
                .append(getString(R.string.total_hero_xp))
                .append(" ")
                .append(getController().getStatisticsValue(LifeController.TOTAL_HERO_XP_TAG))
                .append("\n")
                .append(getString(R.string.total_skills_xp))
                .append(" ")
                .append(getController().getStatisticsValue(LifeController.TOTAL_SKILLS_XP_TAG))
                .append("\n")
                .append(getString(R.string.achievements_unlocked))
                .append(" ")
                .append((int)getController().getStatisticsValue(LifeController.ACHIEVEMENTS_COUNT_TAG))
                .append("\n")
                .append(getString(R.string.xp_multiplier))
                .append(" ")
                .append(getController().getStatisticsValue(LifeController.XP_MULTIPLIER_TAG))
                .append("\n");
        statisticsTV.setText(sb.toString());

        charsChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new CharacteristicsChartFragment(), null);
            }
        });

        skillsChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new SkillsChartFragment(), null);
            }
        });

        tasksPerDayChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new TasksPerDayChartFragment(), null);
            }
        });

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.statistics));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
