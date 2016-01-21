package com.levor.liferpgtasks.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.dataBase.CharacteristicsCursorWrapper;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;
import com.levor.liferpgtasks.dataBase.DataBaseSchema.*;
import com.levor.liferpgtasks.dataBase.HeroCursorWrapper;
import com.levor.liferpgtasks.dataBase.MiscCursorWrapper;
import com.levor.liferpgtasks.dataBase.SkillsCursorWrapper;
import com.levor.liferpgtasks.dataBase.TasksCursorWrapper;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static LifeEntity getInstance(Context context){
        if (lifeEntity == null){
            lifeEntity = new LifeEntity(context);
        }
        return lifeEntity;
    }

    private LifeEntity(Context context) {
        database = new DataBaseHelper(context.getApplicationContext()).getWritableDatabase();

        String count = "SELECT count(*) FROM " + HeroTable.NAME;
        Cursor cursor = database.rawQuery(count, null);
        cursor.moveToFirst();
        if(cursor.getInt(0) < 1) {
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

            addTask(context.getString(R.string.read_book), -1, Task.EASY, Task.EASY, today, true,
                    getSkillByTitle(context.getString(R.string.erudition)));
            addTask(context.getString(R.string.learn_spanish), -1, Task.MEDIUM, Task.MEDIUM, tomorrow, true,
                    getSkillByTitle(context.getString(R.string.spanish)));
            addTask(context.getString(R.string.perform_workout), 1, Task.HARD, Task.HARD, today, true,
                    getSkillByTitle(context.getString(R.string.powerlifting)));
            addTask(context.getString(R.string.morning_running), 25, Task.INSANE, Task.INSANE, tomorrow, true,
                    getSkillByTitle(context.getString(R.string.running)));

            addHero(new Hero(0, 0, 1, context.getString(R.string.default_hero_name)));
        } else {
            hero = getHero();
            characteristics = getCharacteristics();
            skills = getSkills();
            tasks = getTasks();
            getMiscFromDB();

            //adding new characteristic for new version (1.0.2)
            Characteristic health = new Characteristic(context.getString(R.string.health), 1);
            if (!characteristics.contains(health)){
                addCharacteristic(health);
            }
        }
        cursor.close();
    }

    public void addTask(String title,int repeatability, int difficulty, int importance,
                        Date date, boolean notify, Skill ... relatedSkills){
        Task oldTask = getTaskByTitle(title);
        if (oldTask != null) {
            oldTask.setRelatedSkills(Arrays.asList(relatedSkills));
            oldTask.setRepeatability(repeatability);
            oldTask.setDifficulty(difficulty);
            oldTask.setNotify((notify));
            updateTask(oldTask);
        } else {
            UUID id = UUID.randomUUID();
            Task newTask = new Task(title, id, repeatability, difficulty, importance, date, notify, relatedSkills);
            tasks.add(newTask);
            final ContentValues values = getContentValuesForTask(newTask);
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    database.insert(TasksTable.NAME, null, values);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void addTask(String title, int repeatability, int difficulty, int importance,
                        Date date, boolean notify, List<String> relatedSkills){
        Skill[] skills = new Skill[relatedSkills.size()];
        for (int i = 0; i < relatedSkills.size(); i++){
            skills[i] = lifeEntity.getSkillByTitle(relatedSkills.get(i));
        }
        addTask(title, repeatability, difficulty, importance, date, notify, skills);
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
        values.put(TasksTable.Cols.NOTIFY, task.isNotify() ? 1 : 0);
        values.put(TasksTable.Cols.RELATED_SKILLS, task.getRelatedSkillsString());
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
        values.put(SkillsTable.Cols.KEY_CHARACTERISTC_TITLE, skill.getKeyCharacteristic().getTitle());
        return values;
    }

    public void addSkill(String title, Characteristic keyCharacteristic){
        addSkill(title, 1, 0.0f, keyCharacteristic);
    }

    public void addSkill(String title, int level, float sublevel, Characteristic keyCharacteristic){
        Skill oldSkill = getSkillByTitle(title);
        if (oldSkill != null) {
            oldSkill.setLevel(level);
            oldSkill.setSublevel(sublevel);
            oldSkill.setKeyCharacteristic(keyCharacteristic);
            updateSkill(oldSkill);
        } else {
            UUID id = UUID.randomUUID();
            Skill newSkill = new Skill(title, level, sublevel, id, keyCharacteristic);
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
            if (skill.getKeyCharacteristic().equals(ch)){
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
        return values;
    }

    private void getMiscFromDB(){
        MiscCursorWrapper cursor = queryMisc(null, null);
        try{
            cursor.moveToFirst();
            cursor.updateMiscFromDB();
        } finally {
            cursor.close();
        }
    }

    private void updateMiscToDB(){
        final ContentValues values = getContentValuesForMisc();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                database.update(MiscTable.NAME, values, null, null);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void updateAchievementsLevels(String levels){
        Misc.ACHIEVEMENTS_LEVELS = levels;
        updateMiscToDB();
    }
}
