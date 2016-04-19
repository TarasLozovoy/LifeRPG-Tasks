package com.levor.liferpgtasks.view.fragments.settings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.tasks.ExportImportDBFragment;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsFragment extends DefaultFragment {
    @Bind(R.id.statistics_layout)           View statisticsView;
    @Bind(R.id.achievements_layout)         View achievementsView;
    @Bind(R.id.export_import_db_layout)     View exportImportDBView;
    @Bind(R.id.donate_layout)               View donateView;
    @Bind(R.id.donate_text_view)            TextView donateTextView;
    @Bind(R.id.reset_layout)                View resetView;
    @Bind(R.id.about_layout)                View aboutView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);

        donateTextView.setText(getCurrentActivity().isPremium() ? getString(R.string.donate) : getString(R.string.remove_ads));

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

        donateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new DonationFragment(), null);
            }
        });

        resetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder hardAlertBuilder = new AlertDialog.Builder(getCurrentActivity());
                final AlertDialog hardResetAlert = hardAlertBuilder.setTitle(R.string.hard_reset)
                        .setMessage(R.string.hard_reset_message)
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
                        .create();
                AlertDialog.Builder softAlertBuilder = new AlertDialog.Builder(getCurrentActivity());
                final AlertDialog softResetAlert = softAlertBuilder.setTitle(R.string.soft_reset)
                        .setMessage(R.string.soft_reset_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getController().removeAllAppProgress();

                                SharedPreferences prefs = getCurrentActivity()
                                        .getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                                prefs.edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false).apply();

                                Toast.makeText(getContext(), getString(R.string.reset_performed), Toast.LENGTH_LONG)
                                        .show();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .create();

                AlertDialog.Builder initialAlertBuilder = new AlertDialog.Builder(getCurrentActivity());
                initialAlertBuilder.setTitle(R.string.initial_reset)
                        .setMessage(R.string.initial_reset_message)
                        .setPositiveButton(R.string.hard_reset_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hardResetAlert.show();
                            }
                        })
                        .setNegativeButton(R.string.soft_reset_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                softResetAlert.show();
                            }
                        })
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
