package com.levor.liferpgtasks.view.fragments.tasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TasksPerDayChartFragment extends DefaultFragment {
    @Bind(R.id.line_chart)      LineChart lineChart;
    @Bind(R.id.ad_view)         AdView adView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks_per_day_chart, container, false);
        ButterKnife.bind(this, view);
        createChart();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }
        });
        getController().loadNewAdBanner(adView);

        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Tasks Per Day Chart Fragment");
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adView != null) {
            adView.pause();
        }
    }

    private void createChart() {
        List<Entry> values = new ArrayList<>();
        List<String> dateStrings = new ArrayList<>();
        Map<LocalDate, Integer> tasksPerDayMap = getController().getTasksPerDayMap();
        int counter = 0;
        for (Map.Entry<LocalDate, Integer> e : tasksPerDayMap.entrySet()) {
            values.add(new Entry(e.getValue(), counter));
            dateStrings.add(e.getKey().toString());
            counter++;
        }

        LineDataSet lineDataSet = new LineDataSet(values, getString(R.string.tasks_per_day));
        lineDataSet.setColor(R.color.accent);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        LineData lineData = new LineData(dateStrings, lineDataSet);
        lineChart.setData(lineData);
        lineChart.setVisibleXRangeMaximum(15f);
        lineChart.setVisibleXRangeMinimum(5f);

        if (tasksPerDayMap.size() >= 5) {
            lineChart.moveViewToX(tasksPerDayMap.size() - 6);
        }
        lineChart.invalidate();
    }
}
