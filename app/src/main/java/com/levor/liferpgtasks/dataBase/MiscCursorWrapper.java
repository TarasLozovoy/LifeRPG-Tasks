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

    public void updateMiscFromDB() {
        Misc.ACHIEVEMENTS_LEVELS = getString(getColumnIndex(MiscTable.Cols.ACHIEVES_LEVELS));
        Misc.STATISTICS_NUMBERS = getString(getColumnIndex(MiscTable.Cols.STATISTICS_NUMBERS));
        Misc.HERO_IMAGE_PATH = getString(getColumnIndex(MiscTable.Cols.IMAGE_AVATAR));
        Misc.HERO_IMAGE_MODE = getInt(getColumnIndex(MiscTable.Cols.IMAGE_AVATAR_MODE));
    }
}
