package com.levor.liferpgtasks.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.RewardsController;
import com.levor.liferpgtasks.model.Reward;
import com.levor.liferpgtasks.view.PerformTaskAlertBuilder;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.levor.liferpgtasks.view.fragments.rewards.DetailedRewadsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RewardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Reward> items = new ArrayList<>();
    private MainActivity activity;
    private RewardsController rewardsController;
    private int position;

    private View header;

    public RewardsAdapter(List<String> array, MainActivity activity) {
        this.activity = activity;
        rewardsController = RewardsController.getInstance(activity.getApplicationContext());
        for (int i = 0; i < array.size(); i++) {
            Reward reward = rewardsController.getRewardByTitle(array.get(i));
            if (reward != null) {
                items.add(reward);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            View tasksView = inflater.inflate(R.layout.reward_list_item, parent, false);
            return new ViewHolderItem(tasksView);
        } else if (viewType == TYPE_HEADER) {
            if (header == null) {
                header = new View(activity); //dummy view for recyclerViews without header
            }
            return new ViewHolderHeader(header);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItem) {
            ViewHolderItem viewHolder = ((ViewHolderItem) holder);
            final Reward currentReward = items.get(position - 1);
            viewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UUID rewardID = items.get(position - 1).getId();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DetailedRewadsFragment.SELECTED_REWARD_UUID_TAG, rewardID);
                    activity.showChildFragment(new DetailedRewadsFragment(), bundle);
                }
            });
            viewHolder.titleTextView.setText(currentReward.getTitle());
            viewHolder.costTextView.setText("" + currentReward.getCost());

            viewHolder.claimBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle(R.string.reward_claimed)
                            .setMessage(activity.getString(R.string.reward_claimed_dialog_message, currentReward.getTitle()))
                            .setCancelable(false)
                            .setNeutralButton(activity.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.showInterstitialAd(MainActivity.AdType.PERFORM_TASK);
                                    dialog.dismiss();
                                }
                            });
                    // TODO: 8/23/16 Add sharing to social networks
                    AlertDialog alertDialog = alert.create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            notifyDataSetChanged();
                        }
                    });
                    alertDialog.show();
                    notifyDataSetChanged();

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position - 1;
    }

    public void setHeader(View header) {
        this.header = header;
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView costTextView;
        ImageView goldIconImageView;
        ImageButton claimBtn;
        View root;

        public ViewHolderItem(View view) {
            super(view);
            root = view;
            claimBtn = (ImageButton) view.findViewById(R.id.check_button);
            titleTextView = (TextView) view.findViewById(R.id.list_item_title);
            costTextView = (TextView) view.findViewById(R.id.cost_text_view);
            goldIconImageView = (ImageView) view.findViewById(R.id.gold_coin_icon);
            itemView.setLongClickable(true);
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public ViewHolderHeader(View itemView) {
            super(itemView);
        }
    }
}
