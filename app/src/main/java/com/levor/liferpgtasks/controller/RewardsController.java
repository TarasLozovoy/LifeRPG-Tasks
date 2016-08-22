package com.levor.liferpgtasks.controller;


import android.content.Context;

import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Reward;

import java.util.ArrayList;
import java.util.List;

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
        list.add(new Reward());
        return list;
        // TODO: 8/22/16 remove mock reward
    }

    public Reward getRewardByTitle(String title) {
        return new Reward();
        // TODO: 8/22/16 remove mock reward
    }
}
