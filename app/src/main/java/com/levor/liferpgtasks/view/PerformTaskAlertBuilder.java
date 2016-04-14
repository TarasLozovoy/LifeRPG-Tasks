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
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.view.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;


public class PerformTaskAlertBuilder extends AlertDialog.Builder {
    private LifeController lifeController;
    private AlertDialog alert;
    private Context context;
    private Task task;

    public PerformTaskAlertBuilder(final Context context, final Task t, View root) {
        super(context);
        this.context = context;
        this.task = t;
        StringBuilder sb = new StringBuilder();

        lifeController = LifeController.getInstance(context.getApplicationContext());
        Map<Skill, Integer> skillsLevels = new HashMap<>();
        for (Skill sk : task.getRelatedSkillsList()) {
            skillsLevels.put(sk, sk.getLevel());
        }
        double oldHeroXP = lifeController.getHeroXp();
        double oldHeroLevel = lifeController.getHeroLevel();

        //performing task
        lifeController.performTask(task);
        if (oldHeroLevel < lifeController.getHeroLevel()) {
            Toast.makeText(context, context.getString(R.string.hero_level_increased, lifeController.getHeroName()),
                    Toast.LENGTH_LONG).show();
        }

        double xp = lifeController.getHero().getBaseXP() * t.getMultiplier();
        if (oldHeroXP >= lifeController.getHeroXp() || oldHeroLevel > lifeController.getHeroLevel()) {
            xp = - xp;
        }

        sb.append(root.getResources().getString(R.string.task_performed))
                .append("\n")
                .append(xp >= 0 ? "+" : "")
                .append(root.getResources().getString(R.string.XP_gained, xp));
        for (Map.Entry<Skill, Integer> pair : skillsLevels.entrySet()) {
            Skill sk = pair.getKey();
            int oldLevel = pair.getValue();
            if (sk.getLevel() > oldLevel) {
                sb.append("\n")
                        .append("+")
                        .append(sk.getLevel() - oldLevel)
                        .append(context.getString(R.string.level_short))
                        .append(" ")
                        .append(sk.getTitle())
                        .append("\n+")
                        .append(sk.getKeyCharacteristicsGrowth() * (sk.getLevel() - oldLevel))
                        .append(" ")
                        .append(sk.getKeyCharacteristicsStringForUI());
            } else if (sk.getLevel() < oldLevel) {
                sb.append("\n")
                        .append("-")
                        .append(oldLevel - sk.getLevel())
                        .append(context.getString(R.string.level_short))
                        .append(" ")
                        .append(sk.getTitle())
                        .append("\n")
                        .append(sk.getKeyCharacteristicsGrowth() * (sk.getLevel() - oldLevel))
                        .append(" ")
                        .append(sk.getKeyCharacteristicsStringForUI());
            }
        }

        this.setCancelable(false)
                .setTitle(t.getTitle())
                .setMessage(sb.toString())
                .setNeutralButton(root.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)context).showInterstitialAd(MainActivity.AdType.PERFORM_TASK);
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
                            ((MainActivity)context).showInterstitialAd(MainActivity.AdType.PERFORM_TASK);
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
