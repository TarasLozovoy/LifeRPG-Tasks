package com.levor.liferpg.DataBase;


import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpg.DataBase.DataBaseSchema.CharacteristicsTable;
import com.levor.liferpg.Model.Characteristic;
import com.levor.liferpg.Model.LifeEntity;

public class CharacteristicsCursorWrapper extends CursorWrapper {
    private LifeEntity lifeEntity;

    public CharacteristicsCursorWrapper(Cursor cursor, LifeEntity lifeEntity) {
        super(cursor);
        this.lifeEntity = lifeEntity;
    }

    public Characteristic getCharacteristic() {
        String title = getString(getColumnIndex(CharacteristicsTable.Cols.TITLE));
        int level = getInt(getColumnIndex(CharacteristicsTable.Cols.LEVEL));
        return new Characteristic(title, level);
    }
}
