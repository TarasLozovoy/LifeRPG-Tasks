package com.levor.liferpgtasks.view.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.tasks.ExportImportDBFragment;

import java.util.List;

public class SettingsFragment extends DefaultFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        Button showStatisticsButton = (Button) v.findViewById(R.id.show_statistics_button);
        Button showAchievementsButton = (Button) v.findViewById(R.id.show_achievements_button);
        Button export_importDBButton = (Button) v.findViewById(R.id.export_db_button);
        Button contactButton = (Button) v.findViewById(R.id.contact_button);
        Button playMarketButton = (Button) v.findViewById(R.id.play_market_button);

        showStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new StatisticsFragment(), null);
            }
        });

        showAchievementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new AchievementsFragment(), null);
            }
        });

        export_importDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new ExportImportDBFragment(), null);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.app_email)});
                emailIntent.setType("text/plain");
                final PackageManager pm = getCurrentActivity().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                ResolveInfo best = null;
                for (final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                        best = info;
                if (best != null)
                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                getCurrentActivity().startActivity(emailIntent);
            }
        });

        playMarketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.app_address_on_market)));
                startActivity(browserIntent);
            }
        });

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.settings));
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Settings Fragment");
    }
}
