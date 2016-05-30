package com.levor.liferpgtasks.view.fragments.skills;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.view.Dialogs.KeyCharacteristicsSelectionDialog;
import com.levor.liferpgtasks.view.Dialogs.SkillSelectionDialog;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SkillsChartFragment extends DefaultFragment{
    @Bind(R.id.radar_chart)     RadarChart radarChart;
    @Bind(R.id.fab)             FloatingActionButton fab;


    private ArrayList<String> skillTitles;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        ButterKnife.bind(this, view);
        setupSkillsList();
        createChart();
        fab.setOnClickListener(new FabClickListener());

        setHasOptionsMenu(true);
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Skills Chart Fragment");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getCurrentActivity().showInterstitialAd(MainActivity.AdType.CHARACTERISTICS_CHART);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPressed() {
        getCurrentActivity().showInterstitialAd(MainActivity.AdType.CHARACTERISTICS_CHART);
        getCurrentActivity().showPreviousFragment();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void setupSkillsList() {
        List<Skill> skills = getController().getAllSkills();
        Collections.sort(skills, Skill.TITLE_COMPARATOR);
        skillTitles = new ArrayList<>();

        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            skillTitles.add(skill.getTitle());
        }
    }

    private void createChart() {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < skillTitles.size(); i++) {
            Skill skill = getController().getSkillByTitle(skillTitles.get(i));
            values.add(new Entry(skill.getLevel(), i));
        }

        RadarDataSet radarDataSet = new RadarDataSet(values, getString(R.string.skills_fragment_name));
        radarDataSet.setColor(R.color.dark_grey);
        radarDataSet.setFillColor(R.color.red);
        radarDataSet.setDrawFilled(true);
        RadarData radarData = new RadarData(skillTitles, radarDataSet);
        radarChart.setData(radarData);
        radarChart.setDescription("");
        radarChart.invalidate();
    }

    private class FabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SkillSelectionDialog dialog = new SkillSelectionDialog(getCurrentActivity());
            Bundle b = new Bundle();
            b.putStringArrayList(SkillSelectionDialog.ACTIVE_LIST_TAG, skillTitles);
            dialog.setArguments(b);
            dialog.setListener(new SkillSelectionDialog.SkillSelectionListener() {
                @Override
                public void onNewSkillAdded() {
                    //ignore
                }

                @Override
                public void onClose(boolean increasingSkills, ArrayList<String> titles) {
                    skillTitles = titles;
                    radarChart.clear();
                    createChart();
                }
            });
            dialog.show(getCurrentActivity().getSupportFragmentManager(), "CharacteristicsSelection");
        }
    }
}
