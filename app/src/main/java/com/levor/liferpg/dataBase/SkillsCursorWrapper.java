package com.levor.liferpg.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpg.dataBase.DataBaseSchema.SkillsTable;
import com.levor.liferpg.model.LifeEntity;
import com.levor.liferpg.model.Skill;

import java.util.UUID;

public class SkillsCursorWrapper extends CursorWrapper {
    private LifeEntity lifeEntity;

    public SkillsCursorWrapper(Cursor cursor, LifeEntity lifeEntity) {
        super(cursor);
        this.lifeEntity = lifeEntity;
    }

    public Skill getSkill() {
        String uuid = getString(getColumnIndex(SkillsTable.Cols.UUID));
        String title = getString(getColumnIndex(SkillsTable.Cols.TITLE));
        String keyChar = getString(getColumnIndex(SkillsTable.Cols.KEY_CHARACTERISTC_TITLE));
        int level = getInt(getColumnIndex(SkillsTable.Cols.LEVEL));
        double sublevel = getFloat(getColumnIndex(SkillsTable.Cols.SUBLEVEL));
        return new Skill(title, level, sublevel, UUID.fromString(uuid), lifeEntity.getCharacteristicByTitle(keyChar));
    }
}
