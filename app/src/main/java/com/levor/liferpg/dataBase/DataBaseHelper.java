package com.levor.liferpg.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.levor.liferpg.dataBase.DataBaseSchema.*;

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
                HeroTable.Cols.BASEXP + ", " +
                HeroTable.Cols.XP +
                ")");

        db.execSQL("create table " + CharacteristicsTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                CharacteristicsTable.Cols.TITLE + ", " +
                CharacteristicsTable.Cols.LEVEL +
                ")");

        db.execSQL("create table " + SkillsTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                SkillsTable.Cols.UUID + ", " +
                SkillsTable.Cols.TITLE + ", " +
                SkillsTable.Cols.LEVEL + ", " +
                SkillsTable.Cols.SUBLEVEL + ", " +
                SkillsTable.Cols.KEY_CHARACTERISTC_TITLE +
                ")");

        db.execSQL("create table " + TasksTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                TasksTable.Cols.TITLE + ", " +
                TasksTable.Cols.UUID + ", " +
                TasksTable.Cols.REPEATABILITY + ", " +
                TasksTable.Cols.DIFFICULTY + ", " +
                TasksTable.Cols.IMPORTANCE + ", " +
                TasksTable.Cols.DATE + ", " +
                TasksTable.Cols.RELATED_SKILLS +
                ")");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
