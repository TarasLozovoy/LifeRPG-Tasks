package com.levor.liferpgtasks.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.ShareDialogAdapter;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.view.activities.MainActivity;


public class PerformTaskAlertBuilder extends AlertDialog.Builder {
    private LifeController lifeController;
    private AlertDialog alert;
    private Context context;
    private Task task;

    public PerformTaskAlertBuilder(final Context context, final Task t, View root) {
        super(context);
        this.context = context;
        this.task = t;
        lifeController = LifeController.getInstance(context.getApplicationContext());
        double xp = lifeController.getHero().getBaseXP() * t.getMultiplier();
        this.setCancelable(false)
                .setTitle(t.getTitle())
                .setMessage(root.getResources().getString(R.string.task_performed) + "\n" +
                        root.getResources().getString(R.string.XP_gained, xp))
                .setNeutralButton(root.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)context).showInterstitialAd();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(root.getResources().getString(R.string.share), null);
    }

    @Override
    public AlertDialog create() {
        alert = super.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new ShareClickListener(task.getTitle(), alert));
            }
        });
        return alert;
    }

    @Override
    public AlertDialog show() {
        alert.show();
        return alert;
    }

    private class ShareClickListener implements View.OnClickListener{
        private String taskTitle;
        private AlertDialog dialog;

        public ShareClickListener(String task, AlertDialog dialog){
            this.taskTitle = task;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v){
            if (!lifeController.isInternetConnectionActive()) {
                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder shareDialog = new AlertDialog.Builder(context);
            shareDialog.setAdapter(new ShareDialogAdapter(context, taskTitle), null)
                    .setTitle(context.getString(R.string.share_additional_xp))
                    .setCancelable(false)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity)context).showInterstitialAd();
                            dialog.dismiss();
                        }
                    }).show();
            lifeController.getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(context.getString(R.string.GA_action))
                    .setAction(context.getString(R.string.GA_share_button))
                    .build());
            dialog.dismiss();
        }
    }
}
