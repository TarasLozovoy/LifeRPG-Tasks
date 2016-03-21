package com.levor.liferpgtasks.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.levor.liferpgtasks.dataBase.DataBaseSchema.*;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 7;
    public static final String DATABASE_NAME = "RealLifeBase.db";

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
                HeroTable.Cols.MONEY + ", " +
                HeroTable.Cols.XP +
                ")");

        db.execSQL("create table " + CharacteristicsTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                CharacteristicsTable.Cols.TITLE + ", " +
                CharacteristicsTable.Cols.LEVEL + ", " +
                CharacteristicsTable.Cols.ID + " TEXT DEFAULT ''" +
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
                TasksTable.Cols.NOTIFY + ", " +
                TasksTable.Cols.RELATED_SKILLS + ", " +
                TasksTable.Cols.REPEAT_INDEX + ", " +
                TasksTable.Cols.REPEAT_DAYS_OF_WEEK + ", " +
                TasksTable.Cols.REPEAT_MODE + ", " +
                TasksTable.Cols.DATE_MODE + ", " +
                TasksTable.Cols.HABIT_DAYS + ", " +
                TasksTable.Cols.HABIT_DAYS_LEFT + ", " +
                TasksTable.Cols.HABIT_START_DATE + ", " +
                TasksTable.Cols.FINISH_DATE  + " INTEGER" +
                ")");

        //v2+
        db.execSQL("create table " + MiscTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                MiscTable.Cols.IMAGE_AVATAR + ", " +
                MiscTable.Cols.STATISTICS_NUMBERS + ", " +
                MiscTable.Cols.ACHIEVES_LEVELS +
                ")");

        //v5+
        db.execSQL("create table " + TasksPerDayTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                TasksPerDayTable.Cols.DATE + " TEXT, " +
                TasksPerDayTable.Cols.TASKS_PERFORMED +
                ")");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("create table " + MiscTable.NAME + " (" +
                        " _id integer primary key autoincrement, " +
                        MiscTable.Cols.IMAGE_AVATAR + ", " +
                        MiscTable.Cols.STATISTICS_NUMBERS + ", " +
                        MiscTable.Cols.ACHIEVES_LEVELS +
                        ")");
            case 2:
                db.execSQL("alter table " + TasksTable.NAME + " add column " +
                        TasksTable.Cols.REPEAT_INDEX);
                db.execSQL("alter table " + TasksTable.NAME + " add column " +
                        TasksTable.Cols.REPEAT_DAYS_OF_WEEK);
                db.execSQL("alter table " + TasksTable.NAME + " add column " +
                        TasksTable.Cols.REPEAT_MODE);
                db.execSQL("alter table " + TasksTable.NAME + " add column " +
                        TasksTable.Cols.DATE_MODE);
            case 3:
                db.execSQL("alter table " + TasksTable.NAME + " add column " +
                        TasksTable.Cols.HABIT_DAYS);
                db.execSQL("alter table " + TasksTable.NAME + " add column " +
                        TasksTable.Cols.HABIT_DAYS_LEFT);
                db.execSQL("alter table " + TasksTable.NAME + " add column " +
                        TasksTable.Cols.HABIT_START_DATE);
            case 4:
                db.execSQL("create table " + TasksPerDayTable.NAME + " (" +
                        " _id integer primary key autoincrement, " +
                        TasksPerDayTable.Cols.DATE + " TEXT, " +
                        TasksPerDayTable.Cols.TASKS_PERFORMED +
                        ")");
            case 5:
                db.execSQL("alter table " + TasksTable.NAME + " add column " +
                        TasksTable.Cols.FINISH_DATE + " INTEGER");
            case 6:
                db.execSQL("alter table " + CharacteristicsTable.NAME + " add column " +
                        CharacteristicsTable.Cols.ID + " TEXT DEFAULT ''");
        }
    }
}
