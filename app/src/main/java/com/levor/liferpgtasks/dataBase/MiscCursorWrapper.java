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
        String achievs = getString(getColumnIndex(MiscTable.Cols.ACHIEVES_LEVELS));
        Misc.ACHIEVEMENTS_LEVELS = achievs.isEmpty() ? null : achievs;

    }
}
