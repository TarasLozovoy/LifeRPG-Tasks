package com.levor.liferpgtasks.controller;


import android.content.Context;

import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Reward;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RewardsController {

    private LifeEntity lifeEntity;
    private Context context;

    private static RewardsController rewardsController;
    public static RewardsController getInstance(Context context){
        if (rewardsController == null){
            rewardsController = new RewardsController(context);
        }
        return rewardsController;
    }

    private RewardsController(Context context) {
        lifeEntity = LifeEntity.getInstance(context);
        this.context = context;
    }

    public List<Reward> getAllRewards() {
        return lifeEntity.getRewards();
    }

    public Reward getRewardByTitle(String title) {
        for (Reward r : getAllRewards()) {
            if (r.getTitle().equals(title)) {
                return r;
            }
        }
        return null;
    }

    public Reward getRewardByID(UUID id) {
        return lifeEntity.getRewardByID(id);
    }

    public void addReward(Reward reward) {
        lifeEntity.addReward(reward);
    }

    public void updateReward(Reward reward) {
        lifeEntity.updateReward(reward);
    }

    public void removeReward(Reward reward) {
        lifeEntity.removeReward(reward);
    }

    public void claimReward(Reward reward) {
        if (reward.getMode() != Reward.Mode.INFINITE) {
            reward.setDone(true);
        }
        LifeController.getInstance(context).getHero().removeMoney(reward.getCost());
        updateReward(reward);
    }

    public void unclaim(Reward reward) {
        if (reward.isDone()) {
            reward.setDone(false);
            LifeController.getInstance(context).getHero().addMoney(reward.getCost());
            lifeEntity.updateReward(reward);
        }
    }
}
