package com.levor.liferpg.dataBase;


import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpg.dataBase.DataBaseSchema.CharacteristicsTable;
import com.levor.liferpg.model.Characteristic;
import com.levor.liferpg.model.LifeEntity;

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
