package com.levor.liferpg.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.levor.liferpg.DataBase.DataBaseSchema.HeroTable;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "RealLifeBase.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + HeroTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                HeroTable.Cols.NAME + ", " +
                HeroTable.Cols.LEVEL + ", " +
                HeroTable.Cols.XP +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
