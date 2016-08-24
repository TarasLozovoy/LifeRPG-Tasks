package com.levor.liferpgtasks.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Reward;

import java.util.UUID;

public class RewardsCursorWrapper extends CursorWrapper {
    private LifeEntity lifeEntity;

    public RewardsCursorWrapper(Cursor cursor, LifeEntity lifeEntity) {
        super(cursor);
        this.lifeEntity = lifeEntity;
    }

    public Reward getReward() {
        String title = getString(getColumnIndex(DataBaseSchema.RewardsTable.Cols.TITLE));
        int cost = getInt(getColumnIndex(DataBaseSchema.RewardsTable.Cols.COST));
        String idString = getString(getColumnIndex(DataBaseSchema.RewardsTable.Cols.ID));
        String description = getString(getColumnIndex(DataBaseSchema.RewardsTable.Cols.DESCRIPTION));
        boolean done = getInt(getColumnIndex(DataBaseSchema.RewardsTable.Cols.DONE)) == 1;
        UUID id;
        Reward reward = new Reward(title);
        if (!idString.isEmpty()) {
            id = UUID.fromString(idString);
        } else {
            id = UUID.randomUUID();
        }
        reward.setId(id);
        reward.setCost(cost);
        reward.setDescription(description == null ? "" : description);
        reward.setDone(done);
        return reward;
    }
}