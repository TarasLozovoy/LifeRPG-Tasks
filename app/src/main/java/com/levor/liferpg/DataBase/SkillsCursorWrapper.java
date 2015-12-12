package com.levor.liferpg.DataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpg.DataBase.DataBaseSchema.SkillsTable;
import com.levor.liferpg.Model.LifeEntity;
import com.levor.liferpg.Model.Skill;

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
