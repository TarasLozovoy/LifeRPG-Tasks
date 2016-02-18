package com.levor.liferpgtasks.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.TimeUnitUtils;
import com.levor.liferpgtasks.dataBase.CharacteristicsCursorWrapper;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;
import com.levor.liferpgtasks.dataBase.DataBaseSchema.*;
import com.levor.liferpgtasks.dataBase.HeroCursorWrapper;
import com.levor.liferpgtasks.dataBase.MiscCursorWrapper;
import com.levor.liferpgtasks.dataBase.SkillsCursorWrapper;
import com.levor.liferpgtasks.dataBase.TasksCursorWrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LifeEntity {
    private SQLiteDatabase database;

    private static LifeEntity lifeEntity;

    private List<Task> tasks;
    private List<Skill> skills;
    private List<Characteristic> characteristics;
    private Hero hero;
    private Context context;

    public static LifeEntity getInstance(Context context){
        if (lifeEntity == null){
            lifeEntity = new LifeEntity(context);
        }
        return lifeEntity;
    }

    private LifeEntity(Context context) {
        this.context = context;
        openDBConnection();

        String count = "SELECT count(*) FROM " + HeroTable.NAME;
        Cursor cursor = database.rawQuery(count, null);
        cursor.moveToFirst();
        if(cursor.getInt(0) < 1) {
            firstLaunchPreSetup();
        } else {
            hero = getHero();
            characteristics = getCharacteristics();
            skills = getSkills();
            tasks = getTasks();
            getMiscFromDB();    //added for version 1.0.2

            //adding new characteristic for new version (1.0.2)
            Characteristic health = new Characteristic(context.getString(R.string.health), 1);
            if (!characteristics.contains(health)){
                addCharacteristic(health);
            }
        }
        cursor.close();
    }

    private void firstLaunchPreSetup(){
        characteristics = new ArrayList<>();
        skills = new ArrayList<>();
        tasks = new ArrayList<>();
        Characteristic intelligence = new Characteristic(context.getString(R.string.intelligence), 1);
        Characteristic wisdom = new Characteristic(context.getString(R.string.wisdom), 1);
        Characteristic strength = new Characteristic(context.getString(R.string.strength), 1);
        Characteristic stamina = new Characteristic(context.getString(R.string.stamina), 1);
        Characteristic health = new Characteristic(context.getString(R.string.health), 1);
        Characteristic dexterity = new Characteristic(context.getString(R.string.dexterity), 1);
        Characteristic perception = new Characteristic(context.getString(R.string.perception), 1);
        Characteristic memory = new Characteristic(context.getString(R.string.memory), 1);
        Characteristic charisma = new Characteristic(context.getString(R.string.charisma), 1);

        addCharacteristic(intelligence);
        addCharacteristic(wisdom);
        addCharacteristic(strength);
        addCharacteristic(stamina);
        addCharacteristic(health);
        addCharacteristic(dexterity);
        addCharacteristic(perception);
        addCharacteristic(memory);
        addCharacteristic(charisma);

        addSkill(context.getString(R.string.erudition), wisdom);
        addSkill(context.getString(R.string.spanish), intelligence);
        addSkill(context.getString(R.string.powerlifting), strength);
        addSkill(context.getString(R.string.running), stamina);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, 1);
        Date today = c.getTime();
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        Task task1 = new Task(context.getString(R.string.read_book));
        task1.setDate(today);
        task1.setDateMode(Task.DateMode.WHOLE_DAY);
        task1.setRepeatability(-1);
        task1.setRepeatMode(Task.RepeatMode.EVERY_NTH_DAY);
        task1.setRepeatIndex(1);
        task1.setDifficulty(Task.LOW);
        task1.setImportance(Task.LOW);
        task1.setNotifyDelta(4 * TimeUnitUtils.HOUR);
        task1.addRelatedSkill(getSkillByTitle(context.getString(R.string.erudition)));

        Task task2 = new Task(context.getString(R.string.learn_spanish));
        task2.setDate(tomorrow);
        task2.setDateMode(Task.DateMode.WHOLE_DAY);
        task2.setRepeatability(1);
        task2.setRepeatMode(Task.RepeatMode.SIMPLE_REPEAT);
        task2.setRepeatIndex(1);
        task2.setDifficulty(Task.MEDIUM);
        task2.setImportance(Task.MEDIUM);
        task2.setNotifyDelta(5 * TimeUnitUtils.HOUR);
        task2.addRelatedSkill(getSkillByTitle(context.getString(R.string.spanish)));

        Task task3 = new Task(context.getString(R.string.perform_workout));
        task3.setDate(tomorrow);
        task3.setDateMode(Task.DateMode.WHOLE_DAY);
        task3.setRepeatability(1);
        task3.setRepeatMode(Task.RepeatMode.SIMPLE_REPEAT);
        task3.setRepeatIndex(1);
        task3.setDifficulty(Task.HIGH);
        task3.setImportance(Task.HIGH);
        task3.setNotifyDelta(5 * TimeUnitUtils.HOUR);
        task3.addRelatedSkill(getSkillByTitle(context.getString(R.string.powerlifting)));

        Task task4 = new Task(context.getString(R.string.morning_running));
        task4.setDate(tomorrow);
        task4.setDateMode(Task.DateMode.WHOLE_DAY);
        task4.setRepeatability(-1);
        task4.setRepeatMode(Task.RepeatMode.EVERY_NTH_DAY);
        task4.setRepeatIndex(1);
        task4.setDifficulty(Task.INSANE);
        task4.setImportance(Task.INSANE);
        task4.setNotifyDelta(5 * TimeUnitUtils.HOUR);
        task4.addRelatedSkill(getSkillByTitle(context.getString(R.string.running)));

        addTask(task1);
        addTask(task2);
        addTask(task3);
        addTask(task4);

        addHero(new Hero(0, 0, 1, context.getString(R.string.default_hero_name)));

        addMiscToDB(); //added for version 1.0.2
    }

    public void addTask(Task task){
        Task oldTask = getTaskByTitle(task.getTitle());
        if (oldTask != null) {
            oldTask.setDate(task.getDate());
            oldTask.setDateMode(task.getDateMode());
            oldTask.setRepeatability(task.getRepeatability());
            oldTask.setRepeatMode(task.getRepeatMode());
            oldTask.setRepeatDaysOfWeek(task.getRepeatDaysOfWeek());
            oldTask.setRepeatIndex(task.getRepeatIndex());
            oldTask.setDifficulty(task.getDifficulty());
            oldTask.setImportance(task.getImportance());
            oldTask.setNotifyDelta(task.getNotifyDelta());
            updateTask(oldTask);
        } else {
            tasks.add(task);
            final ContentValues values = getContentValuesForTask(task);
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    database.insert(TasksTable.NAME, null, values);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void updateTask(Task task) {
        final String uuid = task.getId().toString();
        if (tasks.remove(task)) {
            tasks.add(task);
            final ContentValues values = getContentValuesForTask(task);
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    database.update(TasksTable.NAME, values, TasksTable.Cols.UUID + " = ?", new String[]{uuid});
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void removeTask(final Task task) {
        tasks.remove(task);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                database.delete(TasksTable.NAME, TasksTable.Cols.UUID + " = ?", new String[]{task.getId().toString()});
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public List<Task> getTasks(){
        if (tasks != null){
            if (tasks.contains(null)) {
                tasks.removeAll(Collections.singleton(null));
            }
            return tasks;
        } else {
            List<Task> tasksList = new ArrayList<>();
            TasksCursorWrapper cursorWrapper = queryTasks(null, null);
            try {
                cursorWrapper.moveToFirst();
                while (!cursorWrapper.isAfterLast()) {
                    tasksList.add(cursorWrapper.getTask());
                    cursorWrapper.moveToNext();
                }
            } finally {
                cursorWrapper.close();
            }
            return tasksList;
        }
    }

    public Task getTaskByID(UUID id) {
        for (Task t : tasks){
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    public Task getTaskByTitle(String s) {
        for (Task t : tasks){
            if (t.getTitle().equals(s)) return t;
        }
        return null;
    }

    private TasksCursorWrapper queryTasks(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                TasksTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new TasksCursorWrapper(cursor, this);
    }

    public ArrayList<Task> getTasksBySkill(Skill sk){
        ArrayList<Task> tasksBySkill = new ArrayList<>();
        for (Task t : getTasks()){
            if (t.getRelatedSkills().contains(sk)){
                tasksBySkill.add(t);
            }
        }
        Collections.sort(tasksBySkill, Task.TITLE_DESC_TASKS_COMPARATOR);
        return tasksBySkill;
    }

    private static ContentValues getContentValuesForTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TasksTable.Cols.TITLE, task.getTitle());
        values.put(TasksTable.Cols.UUID, task.getId().toString());
        values.put(TasksTable.Cols.REPEATABILITY, task.getRepeatability());
        values.put(TasksTable.Cols.DIFFICULTY, task.getDifficulty());
        values.put(TasksTable.Cols.IMPORTANCE, task.getImportance());
        values.put(TasksTable.Cols.DATE, task.getDate().getTime());
        values.put(TasksTable.Cols.NOTIFY, task.getNotifyDelta());
        values.put(TasksTable.Cols.RELATED_SKILLS, task.getRelatedSkillsString());
        values.put(TasksTable.Cols.DATE_MODE, task.getDateMode());
        values.put(TasksTable.Cols.REPEAT_MODE, task.getRepeatMode());
        values.put(TasksTable.Cols.REPEAT_DAYS_OF_WEEK, task.getRepeatDaysOfWeekString());
        values.put(TasksTable.Cols.REPEAT_INDEX, task.getRepeatIndex());
        return values;
    }

    private SkillsCursorWrapper querySkills(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                SkillsTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new SkillsCursorWrapper(cursor, this);
    }

    private static ContentValues getContentValuesForSkill(Skill skill) {
        ContentValues values = new ContentValues();
        values.put(SkillsTable.Cols.TITLE, skill.getTitle());
        values.put(SkillsTable.Cols.UUID, skill.getId().toString());
        values.put(SkillsTable.Cols.LEVEL, skill.getLevel());
        values.put(SkillsTable.Cols.SUBLEVEL, skill.getSublevel());
        values.put(SkillsTable.Cols.KEY_CHARACTERISTC_TITLE, skill.getKeyCharacteristicsString());
        return values;
    }

    public void addSkill(String title, List<Characteristic> characteristicList){
        addSkill(title, 1, 0.0f, characteristicList);
    }

    public void addSkill(String title, Characteristic characteristic){
        List<Characteristic> chars = new ArrayList<>();
        chars.add(characteristic);
        addSkill(title, 1, 0.0f, chars);
    }

    public void addSkill(String title, int level, float sublevel, List<Characteristic> characteristicList){
        Skill oldSkill = getSkillByTitle(title);
        if (oldSkill != null) {
            oldSkill.setLevel(level);
            oldSkill.setSublevel(sublevel);
            oldSkill.setKeyCharacteristicsList(characteristicList);
            updateSkill(oldSkill);
        } else {
            UUID id = UUID.randomUUID();
            Skill newSkill = new Skill(title, level, sublevel, id, characteristicList);
            skills.add(newSkill);
            final ContentValues values = getContentValuesForSkill(newSkill);
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    database.insert(SkillsTable.NAME, null, values);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public List<Skill> getSkills(){
        if (skills != null){
            skills.removeAll(Collections.singleton(null));
            Collections.sort(skills, Skill.LEVEL_COMPARATOR); //change achievements if changing comparator
            return skills;
        }
        List<Skill> skillsList = new ArrayList<>();
        SkillsCursorWrapper cursorWrapper = querySkills(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                skillsList.add(cursorWrapper.getSkill());
                cursorWrapper.moveToNext();
            }} finally {
            cursorWrapper.close();
        }
        skillsList.removeAll(Collections.singleton(null));
        Collections.sort(skillsList, Skill.LEVEL_COMPARATOR);
        return skillsList;
    }

    public Skill getSkillByID(UUID id){
        for (Skill sk : skills){
            if (sk.getId().equals(id)) return sk;
        }
        return null;
    }

    public Skill getSkillByTitle(String title) {
        for (Skill sk : skills){
            if (sk.getTitle().equals(title)) return sk;
        }
        return null;
    }

    public ArrayList<Skill> getSkillsByCharacteristic(Characteristic ch){
        ArrayList<Skill> sk = new ArrayList<>();
        for (Skill skill : getSkills()){
            if (skill.getKeyCharacteristicsList().contains(ch)){
                sk.add(skill);
            }
        }
        return sk;
    }

    public void updateSkill(Skill skill) {
        final String uuid = skill.getId().toString();
        final ContentValues values = getContentValuesForSkill(skill);
        if (skills.remove(skill)) {
            skills.add(skill);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    database.update(SkillsTable.NAME, values, SkillsTable.Cols.UUID + " = ?", new String[]{uuid});
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void removeSkill(final Skill skill) {
        skills.remove(skill);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                database.delete(SkillsTable.NAME, SkillsTable.Cols.UUID + " = ?", new String[]{skill.getId().toString()});
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public List<String> getSkillsTitles() {
        List<String> titles = new ArrayList<>();
        for (Skill s : getSkills()) {
            titles.add(s.getTitle());
        }
        return titles;
    }

    private CharacteristicsCursorWrapper queryCharacteristics(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                CharacteristicsTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CharacteristicsCursorWrapper(cursor, this);
    }

    private static ContentValues getContentValuesForCharacteristic(Characteristic characteristic) {
        ContentValues values = new ContentValues();
        values.put(CharacteristicsTable.Cols.TITLE, characteristic.getTitle());
        values.put(CharacteristicsTable.Cols.LEVEL, characteristic.getLevel());
        return values;
    }

    private void addCharacteristic(Characteristic characteristic){
        characteristics.add(characteristic);
        final ContentValues values = getContentValuesForCharacteristic(characteristic);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                database.insert(CharacteristicsTable.NAME, null, values);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public List<Characteristic> getCharacteristics(){
        if (characteristics != null) {
            Collections.sort(characteristics, Characteristic.LEVEL_COMPARATOR); //change achievements if changing comparator
            return characteristics;
        }
        List<Characteristic> chars = new ArrayList<>();
        CharacteristicsCursorWrapper cursorWrapper = queryCharacteristics(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                chars.add(cursorWrapper.getCharacteristic());
                cursorWrapper.moveToNext();
            }} finally {
            cursorWrapper.close();
        }
        Collections.sort(chars, Characteristic.LEVEL_COMPARATOR);
        return chars;
    }

    public void updateCharacteristic(final Characteristic characteristic) {
        if (characteristics.remove(characteristic)) {
            characteristics.add(characteristic);
            final ContentValues values = getContentValuesForCharacteristic(characteristic);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    database.update(CharacteristicsTable.NAME, values, CharacteristicsTable.Cols.TITLE + " = ?", new String[]{characteristic.getTitle()});
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public Characteristic getCharacteristicByTitle(String title) {
        for (Characteristic ch : getCharacteristics()){
            if (ch.getTitle().equals(title)) return ch;
        }
        return null;
    }

    private HeroCursorWrapper queryHero(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                HeroTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new HeroCursorWrapper(cursor, this);
    }

    private static ContentValues getContentValuesForHero(Hero hero) {
        ContentValues values = new ContentValues();
        values.put(HeroTable.Cols.NAME, hero.getName());
        values.put(HeroTable.Cols.LEVEL, hero.getLevel());
        values.put(HeroTable.Cols.XP, hero.getXp());
        values.put(HeroTable.Cols.BASEXP, hero.getBaseXP());
        values.put(HeroTable.Cols.MONEY, 0.0d);
        return values;
    }

    private void addHero(Hero hero) {
        this.hero = hero;
        final ContentValues values = getContentValuesForHero(hero);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                database.insert(HeroTable.NAME, null, values);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void updateHero(Hero hero) {
        this.hero = hero;
        final ContentValues values = getContentValuesForHero(hero);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                database.update(HeroTable.NAME, values, null, null);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public Hero getHero() {
        if (hero != null) {
            return hero;
        }
        HeroCursorWrapper cursor = queryHero(null, null);
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getHero();
        } finally {
            cursor.close();
        }
    }

    private MiscCursorWrapper queryMisc(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                MiscTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new MiscCursorWrapper(cursor);
    }

    private static ContentValues getContentValuesForMisc() {
        ContentValues values = new ContentValues();
        values.put(MiscTable.Cols.ACHIEVES_LEVELS, Misc.ACHIEVEMENTS_LEVELS);
        values.put(MiscTable.Cols.STATISTICS_NUMBERS, Misc.STATISTICS_NUMBERS);
        values.put(MiscTable.Cols.IMAGE_AVATAR, Misc.HERO_IMAGE_PATH);
        return values;
    }

    private void getMiscFromDB() {
        MiscCursorWrapper cursor = queryMisc(null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.updateMiscFromDB();
            cursor.close();
        } else {
            addMiscToDB();
        }
    }

    private void addMiscToDB(){
        final ContentValues values = getContentValuesForMisc();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                database.insert(MiscTable.NAME, null, values);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void updateMiscToDB(){
        final ContentValues values = getContentValuesForMisc();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                database.update(MiscTable.NAME, values, null, null);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void resetMisc(){
        Misc.ACHIEVEMENTS_LEVELS = null;
        Misc.HERO_IMAGE_PATH = "elegant5.png";
        Misc.STATISTICS_NUMBERS = null;
    }

    public void closeDBConnection(){
        if (database.isOpen()) {
            database.close();
        }
    }

    public void openDBConnection(){
        if (database != null && !database.isOpen()){
            database.close();
        }
        database = new DataBaseHelper(context.getApplicationContext()).getWritableDatabase();
    }

    public void onDBFileUpdated(boolean isFileDeleted){
        hero = null;
        characteristics = null;
        skills = null;
        tasks = null;
        if (isFileDeleted) {
            resetMisc();
            firstLaunchPreSetup();
        } else {
            hero = getHero();
            characteristics = getCharacteristics();
            skills = getSkills();
            tasks = getTasks();
            getMiscFromDB();
        }
    }
}
