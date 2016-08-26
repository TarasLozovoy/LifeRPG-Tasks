package com.levor.liferpgtasks.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.ShareDialogAdapter;
import com.levor.liferpgtasks.controller.AudioController;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.view.activities.MainActivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class PerformTaskAlertBuilder extends AlertDialog.Builder {
    private LifeController lifeController;
    private AlertDialog alert;
    private Context context;
    private Task task;

    public PerformTaskAlertBuilder(final Context context, final Task t) {
        super(context);
        this.context = context;
        this.task = t;

        View dialogView = View.inflate(context, R.layout.perform_task_alert_layout, null);
        TextView gainedXP = (TextView) dialogView.findViewById(R.id.gained_xp);
        TextView gainedGold = (TextView) dialogView.findViewById(R.id.gained_gold);
        TextView heroLevelUp = (TextView) dialogView.findViewById(R.id.hero_level_up);
        TextView gainedSkills = (TextView) dialogView.findViewById(R.id.gained_skills);
        TextView gainedCharacteristics = (TextView) dialogView.findViewById(R.id.gained_characteristics);
        TextView gainedAchievements = (TextView) dialogView.findViewById(R.id.gained_achievements);

        View goldView = dialogView.findViewById(R.id.gold_layout);
        View heroLevelUpView = dialogView.findViewById(R.id.hero_level_up_layout);
        View skillsView = dialogView.findViewById(R.id.skills_layout);
        View characteristicsView = dialogView.findViewById(R.id.characteristics_layout);
        View achievementsView = dialogView.findViewById(R.id.achievements_layout);

        lifeController = LifeController.getInstance(context.getApplicationContext());

        //save old skills levels
        Map<Skill, Integer> skillsOldLevels = new HashMap<>();
        for (Skill sk : task.getRelatedSkillsList()) {
            skillsOldLevels.put(sk, sk.getLevel());
        }

        //save old characteristics levels
        Map<Characteristic, Integer> characteristicsOldLevels = new HashMap<>();
        for (Characteristic ch : lifeController.getCharacteristics()) {
            characteristicsOldLevels.put(ch, ch.getLevel());
        }

        double oldHeroXP = lifeController.getHeroXp();
        double oldHeroLevel = lifeController.getHeroLevel();

        //performing task
        lifeController.performTask(task);

        //hero level up
        boolean levelUp = false;
        int skillsUp = 0;
        if (oldHeroLevel < lifeController.getHeroLevel()) {
            levelUp = true;
            heroLevelUpView.setVisibility(View.VISIBLE);
            heroLevelUp.setText(context.getString(R.string.hero_level_increased, lifeController.getHeroName()));
        }

        double xp = lifeController.getHero().getBaseXP() * t.getMultiplier();
        if ((oldHeroXP >= lifeController.getHeroXp() && oldHeroLevel == lifeController.getHeroLevel())
                || oldHeroLevel > lifeController.getHeroLevel()) {
            xp = - xp;
        }

        //xp
        String xpString = context.getResources().getString(R.string.task_performed) + (xp >= 0 ? "+" : "")
                + context.getResources().getString(R.string.XP_gained, xp);
        gainedXP.setText(xpString);

        //gold
        if (t.getMoneyReward() > 0d) {
            DecimalFormat df = new DecimalFormat("#.##");
            goldView.setVisibility(View.VISIBLE);
            gainedGold.setText("+" + df.format(t.getMoneyReward()));
        }

        //skills
        StringBuilder skillsString = new StringBuilder();
        for (Map.Entry<Skill, Integer> pair : skillsOldLevels.entrySet()) {
            Skill sk = pair.getKey();
            int oldLevel = pair.getValue();
            if (sk.getLevel() > oldLevel) {
                skillsString.append("+")
                        .append(sk.getLevel() - oldLevel)
                        .append(" ")
                        .append(sk.getTitle())
                        .append("\n");
                skillsUp++;
            } else if (sk.getLevel() < oldLevel) {
                skillsString.append("-")
                        .append(oldLevel - sk.getLevel())
                        .append(" ")
                        .append(sk.getTitle())
                        .append("\n");
            }
        }
        if (!skillsString.toString().isEmpty()) {
            skillsString.deleteCharAt(skillsString.length() - 1);
            skillsView.setVisibility(View.VISIBLE);
            gainedSkills.setText(skillsString.toString());
        }

        //characteristics
        StringBuilder characteristicsString = new StringBuilder();
        for (Map.Entry<Characteristic, Integer> pair : characteristicsOldLevels.entrySet()) {
            Characteristic ch = pair.getKey();
            int level = pair.getValue();
            if (level < ch.getLevel()) {
                characteristicsString.append("+")
                        .append(ch.getLevel() - level)
                        .append(" ")
                        .append(ch.getTitle())
                        .append("\n");
            } else if (level > ch.getLevel()) {
                characteristicsString.append("-")
                        .append(level - ch.getLevel())
                        .append(" ")
                        .append(ch.getTitle())
                        .append("\n");
            }
        }
        if (!characteristicsString.toString().isEmpty()) {
            characteristicsString.deleteCharAt(characteristicsString.length() - 1);
            characteristicsView.setVisibility(View.VISIBLE);
            gainedCharacteristics.setText(characteristicsString.toString());
        }

        //achievements
        if (!lifeController.getAchievementsBuffer().isEmpty()) {
            achievementsView.setVisibility(View.VISIBLE);
            gainedAchievements.setText(lifeController.getAchievementsBuffer());
            lifeController.clearAchievementsBuffer();
        }

        this.setCancelable(false)
                .setTitle(t.getTitle())
                .setView(dialogView)
                .setNeutralButton(context.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)context).showInterstitialAd(MainActivity.AdType.PERFORM_TASK);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getResources().getString(R.string.share), null);

        //play some sound for level up and skill up
        if (levelUp || skillsUp > 0) {
            AudioController audioController = AudioController.getInstance(context);

            if (levelUp) {
                audioController.playLevelUpSound();
            } else if (skillsUp > 0) {
                audioController.playSkillUpSound();
            }

        }
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
