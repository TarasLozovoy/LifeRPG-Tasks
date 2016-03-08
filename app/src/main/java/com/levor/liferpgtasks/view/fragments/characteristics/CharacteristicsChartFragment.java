package com.levor.liferpgtasks.view.fragments.characteristics;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.levor.liferpgtasks.R;

import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CharacteristicsChartFragment extends DefaultFragment{
    @Bind(R.id.radar_chart)     RadarChart radarChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characteristics_chart, container, false);
        ButterKnife.bind(this, view);
        createChart();

        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Characteristics Chart Fragment");
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

    private void createChart() {
        ArrayList<Entry> values = new ArrayList<>();
        List<Characteristic> chars = getController().getCharacteristics();
        Collections.sort(chars, Characteristic.TITLE_COMPARATOR);
        List<String> titles = new ArrayList<>();

        for (int i = 0; i < chars.size(); i++) {
            Characteristic ch = chars.get(i);
            values.add(new Entry(ch.getLevel(), i));
            titles.add(ch.getTitle());
        }

        RadarDataSet radarDataSet = new RadarDataSet(values, getString(R.string.characteristics_fragment_name));
        radarDataSet.setColor(R.color.accent);
        RadarData radarData = new RadarData(titles, radarDataSet);
        radarChart.setData(radarData);
        radarChart.invalidate();
    }
}
