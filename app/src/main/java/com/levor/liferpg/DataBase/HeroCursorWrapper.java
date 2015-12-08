package com.levor.liferpg.DataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpg.DataBase.DataBaseSchema.HeroTable;
import com.levor.liferpg.Model.Hero;
import com.levor.liferpg.Model.LifeEntity;

public class HeroCursorWrapper extends CursorWrapper {
    private LifeEntity lifeEntity;

    public HeroCursorWrapper(Cursor cursor, LifeEntity lifeEntity) {
        super(cursor);
        this.lifeEntity = lifeEntity;
    }

    public Hero getHero() {
        String name = getString(getColumnIndex(HeroTable.Cols.NAME));
        int level = getInt(getColumnIndex(HeroTable.Cols.LEVEL));
        int xp = getInt(getColumnIndex(HeroTable.Cols.XP));
        return new Hero(level, xp, name);
    }
}
