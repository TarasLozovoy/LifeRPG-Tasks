package com.levor.liferpg.Model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.levor.liferpg.DataBase.CharacteristicsCursorWrapper;
import com.levor.liferpg.DataBase.DataBaseHelper;
import com.levor.liferpg.DataBase.DataBaseSchema.*;
import com.levor.liferpg.DataBase.HeroCursorWrapper;
import com.levor.liferpg.DataBase.SkillsCursorWrapper;
import com.levor.liferpg.DataBase.TasksCursorWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
            hero = new Hero();
            characteristics = new ArrayList<>();
            skills = new ArrayList<>();
            tasks = new ArrayList<>();
            Characteristic intelligence = new Characteristic("Intelligence", 1);
            Characteristic wisdom = new Characteristic("Wisdom", 1);
            Characteristic strength = new Characteristic("Strength", 1);
            Characteristic stamina = new Characteristic("Stamina", 1);
            Characteristic dexterity = new Characteristic("Dexterity", 1);
            Characteristic perception = new Characteristic("Perception", 1);
            Characteristic memory = new Characteristic("Memory", 1);
            Characteristic charisma = new Characteristic("Charisma", 1);

            addCharacteristic(intelligence);
            addCharacteristic(wisdom);
            addCharacteristic(strength);
            addCharacteristic(stamina);
            addCharacteristic(dexterity);
            addCharacteristic(perception);
            addCharacteristic(memory);
            addCharacteristic(charisma);

            addSkill("Android", intelligence);
            addSkill("Java", intelligence);
            addSkill("Erudition", wisdom);
            addSkill("English", intelligence);
            addSkill("Powerlifting", strength);
            addSkill("Roller skating", stamina);
            addSkill("Running", stamina);

            addTask("Learn Android", -1, getSkillByTitle("Android"));
            addTask("Learn Java", 0, getSkillByTitle("Java"));
            addTask("Fix bug on Android", 1, getSkillByTitle("Android"));
            addTask("Fix bug on Java", 25, getSkillByTitle("Java"));

            addHero(new Hero());
        } else {
            hero = getHero();
            characteristics = getCharacteristics();
            skills = getSkills();
            tasks = getTasks();
        }
        cursor.close();
    }

    public void addTask(String title,int repeatability, Skill ... relatedSkills){
        Task oldTask = getTaskByTitle(title);
        if (oldTask != null) {
            oldTask.setRelatedSkills(Arrays.asList(relatedSkills));
            oldTask.setRepeatability(repeatability);
            updateTask(oldTask);
        } else {
            UUID id = UUID.randomUUID();
            Task newTask = new Task(title, id, repeatability, relatedSkills);
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

    public void addTask(String title, int repeatability, List<String> relatedSkills){
        Skill[] skills = new Skill[relatedSkills.size()];
        for (int i = 0; i < relatedSkills.size(); i++){
            skills[i] = lifeEntity.getSkillByTitle(relatedSkills.get(i));
        }
        addTask(title, repeatability, skills);
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
            Collections.sort(tasks, Task.COMPARATOR);
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
        Collections.sort(tasksBySkill, Task.COMPARATOR);
        return tasksBySkill;
    }

    private static ContentValues getContentValuesForTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TasksTable.Cols.TITLE, task.getTitle());
        values.put(TasksTable.Cols.UUID, task.getId().toString());
        values.put(TasksTable.Cols.REPEATABILITY, task.getRepeatability());
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
        addSkill(title, 1, 0, keyCharacteristic);
    }

    public void addSkill(String title, int level, int sublevel, Characteristic keyCharacteristic){
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

    public Map<String, Integer[]> getSkillsTitlesAndLevels() {
        Map<String, Integer[]> map = new TreeMap<>();
        for (Skill s : getSkills()) {
            map.put(s.getTitle(), new Integer[]{s.getLevel(), s.getSublevel()});
        }
        return map;
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
}
