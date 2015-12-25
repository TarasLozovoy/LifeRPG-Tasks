package com.levor.liferpgtasks.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpgtasks.dataBase.DataBaseSchema.HeroTable;
import com.levor.liferpgtasks.model.Hero;
import com.levor.liferpgtasks.model.LifeEntity;

public class HeroCursorWrapper extends CursorWrapper {
    private LifeEntity lifeEntity;

    public HeroCursorWrapper(Cursor cursor, LifeEntity lifeEntity) {
        super(cursor);
        this.lifeEntity = lifeEntity;
    }

    public Hero getHero() {
        String name = getString(getColumnIndex(HeroTable.Cols.NAME));
        int level = getInt(getColumnIndex(HeroTable.Cols.LEVEL));
        double xp = getInt(getColumnIndex(HeroTable.Cols.XP));
        double baseXP = getInt(getColumnIndex(HeroTable.Cols.BASEXP));
        return new Hero(level, xp, baseXP, name);
    }
}
