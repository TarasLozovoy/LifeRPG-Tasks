package com.levor.liferpgtasks.dataBase;


import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpgtasks.dataBase.DataBaseSchema.CharacteristicsTable;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.LifeEntity;

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
