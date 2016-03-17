package com.levor.liferpgtasks.view.fragments.settings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.tasks.ExportImportDBFragment;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsFragment extends DefaultFragment {
    @Bind(R.id.statistics_layout)           View statisticsView;
    @Bind(R.id.achievements_layout)         View achievementsView;
    @Bind(R.id.export_import_db_layout)     View exportImportDBView;
    @Bind(R.id.reset_layout)                View resetView;
    @Bind(R.id.about_layout)                View aboutView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);

        statisticsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new StatisticsFragment(), null);
            }
        });

        achievementsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new AchievementsFragment(), null);
            }
        });

        exportImportDBView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new ExportImportDBFragment(), null);
            }
        });

        resetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getCurrentActivity());
                alertBuilder.setTitle(R.string.reset)
                        .setMessage(R.string.reset_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getController().closeDBConnection();
                                File db = getCurrentActivity().getDatabasePath(DataBaseHelper.DATABASE_NAME);
                                db.delete();
                                getController().openDBConnection();
                                getCurrentActivity().onDBFileUpdated(true);

                                SharedPreferences prefs = getCurrentActivity()
                                        .getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                                prefs.edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false).apply();

                                Toast.makeText(getContext(), getString(R.string.reset_performed), Toast.LENGTH_LONG)
                                        .show();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });

        aboutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new AboutFragment(), null);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
