package com.levor.liferpgtasks.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpgtasks.dataBase.DataBaseSchema.MiscTable;
import com.levor.liferpgtasks.model.Misc;


public class MiscCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public MiscCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public void updateMiscFromDB(){
        int achievsIndex = getColumnIndex(MiscTable.Cols.ACHIEVES_LEVELS);
        if (achievsIndex >= 0){
            Misc.ACHIEVEMENTS_LEVELS = getString(achievsIndex);
        } else {
            Misc.ACHIEVEMENTS_LEVELS = null;
        }
    }
}
