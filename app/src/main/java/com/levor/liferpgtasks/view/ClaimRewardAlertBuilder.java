package com.levor.liferpgtasks.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.TextUtils;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.controller.RewardsController;
import com.levor.liferpgtasks.model.Reward;
import com.levor.liferpgtasks.view.activities.MainActivity;

import java.text.DecimalFormat;

public class ClaimRewardAlertBuilder extends AlertDialog.Builder {

    public ClaimRewardAlertBuilder(final Context context, final Reward r) {
        super(context);
        View dialogView = View.inflate(getContext(), R.layout.claim_reward_dialog, null);

        TextView goldBeforeTextView = (TextView) dialogView.findViewById(R.id.gold_before);
        TextView goldAfterTextView = (TextView) dialogView.findViewById(R.id.gold_after);

        DecimalFormat df = TextUtils.DECIMAL_FORMAT;
        LifeController lifeController = LifeController.getInstance(context);
        goldBeforeTextView.setText(df.format(lifeController.getHero().getMoney()));
        RewardsController.getInstance(context).claimReward(r);
        goldAfterTextView.setText(df.format(lifeController.getHero().getMoney()));

        setView(dialogView);
        setCancelable(false);
        setTitle(r.getTitle());
        setNeutralButton(context.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) context).showInterstitialAd(MainActivity.AdType.PERFORM_TASK);
                dialog.dismiss();
            }
        });

        // TODO: 8/23/16 Add sharing to social networks
    }
}
