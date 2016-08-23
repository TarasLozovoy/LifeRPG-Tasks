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
        List<Reward> list = new ArrayList<>();
        list.add(new Reward(new Date().toString()));
        Reward reward = new Reward("Claimed one");
        reward.setDone(true);
        list.add(reward);
        return list;
        // TODO: 8/22/16 remove mock reward
    }

    public Reward getRewardByTitle(String title) {
        return new Reward(new Date().toString());
        // TODO: 8/22/16 remove mock reward
    }

    public Reward getRewardByID(UUID id) {
        return new Reward(new Date().toString());
        // TODO: 8/22/16 remove mock reward
    }

    public void addReward(Reward reward) {
        // TODO: 8/23/16 adding reward to DB
    }

    public void updateReward(Reward reward) {
        // TODO: 8/23/16 adding reward to DB
    }

    public void removeReward(Reward reward) {
        // TODO: 8/23/16 adding reward to DB
    }

    public void claimReward(Reward reward) {
        // TODO: 8/23/16 adding reward to DB
    }
}
