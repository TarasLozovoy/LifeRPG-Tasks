package com.levor.liferpgtasks.view.fragments.characteristics;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.levor.liferpgtasks.R;

import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.view.Dialogs.KeyCharacteristicsSelectionDialog;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CharacteristicsChartFragment extends DefaultFragment{
    @Bind(R.id.radar_chart)     RadarChart radarChart;


    private ArrayList<String> charTitles;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characteristics_chart, container, false);
        ButterKnife.bind(this, view);
        setupCharacteristicsList();
        createChart();

        setHasOptionsMenu(true);
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Characteristics Chart Fragment");
        showFab();
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

    private void showFab() {
        if (getCurrentActivity() == null) return;
        getCurrentActivity().showFab(true);
        getCurrentActivity().setFabImage(R.drawable.ic_mode_edit_black_24dp);
        getCurrentActivity().setFabClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyCharacteristicsSelectionDialog dialog = new KeyCharacteristicsSelectionDialog();
                Bundle b = new Bundle();
                b.putStringArrayList(KeyCharacteristicsSelectionDialog.CHARS_LIST, charTitles);
                dialog.setArguments(b);
                dialog.setListener(new KeyCharacteristicsSelectionDialog.KeyCharacteristicsChangedListener() {
                    @Override
                    public void onChanged(ArrayList<String> charsTitles) {
                        charTitles = charsTitles;
                        radarChart.clear();
                        createChart();
                    }
                });
                dialog.show(getCurrentActivity().getSupportFragmentManager(), "CharacteristicsSelection");
            }
        });
    }

    @Override
    public boolean isFabVisible() {
        return true;
    }

    private void setupCharacteristicsList() {
        List<Characteristic> chars = getController().getCharacteristics();
        Collections.sort(chars, Characteristic.TITLE_COMPARATOR);
        charTitles = new ArrayList<>();

        for (int i = 0; i < chars.size(); i++) {
            Characteristic ch = chars.get(i);
            charTitles.add(ch.getTitle());
        }
    }

    private void createChart() {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < charTitles.size(); i++) {
            Characteristic ch = getController().getCharacteristicByTitle(charTitles.get(i));
            values.add(new Entry(ch.getLevel(), i));
        }

        RadarDataSet radarDataSet = new RadarDataSet(values, getString(R.string.characteristics_fragment_name));
        radarDataSet.setColor(R.color.dark_grey);
        radarDataSet.setFillColor(R.color.red);
        radarDataSet.setDrawFilled(true);
        RadarData radarData = new RadarData(charTitles, radarDataSet);
        radarChart.setData(radarData);
        radarChart.setDescription("");
        radarChart.invalidate();
    }
}
