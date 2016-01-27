package com.levor.liferpgtasks.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Task {
    public final static int EASY = 0;
    public final static int MEDIUM = 1;
    public final static int HARD = 2;
    public final static int INSANE = 3;
    private String title;
    private List<Skill> relatedSkills = new ArrayList<>();
    private UUID id;
    private int repeatability = -1;
    private int difficulty = EASY;
    private int importance = EASY;
    private Date date;
    private boolean undonable = false;
    private boolean notify = true;

    public static final Comparator<Task> COMPLETION_TASKS_COMPARATOR = new CompletionTasksComparator();
    public static final Comparator<Task> TITLE_ASC_TASKS_COMPARATOR = new TitleAscTasksComparator();
    public static final Comparator<Task> TITLE_DESC_TASKS_COMPARATOR = new TitleDescTasksComparator();
    public static final Comparator<Task> IMPORTANCE_ASC_TASKS_COMPARATOR = new ImportanceAscTasksComparator();
    public static final Comparator<Task> IMPORTANCE_DESC_TASKS_COMPARATOR = new ImportanceDescTasksComparator();
    public static final Comparator<Task> DIFFICULTY_ASC_TASKS_COMPARATOR = new DifficultyAscTasksComparator();
    public static final Comparator<Task> DIFFICULTY_DESC_TASKS_COMPARATOR = new DifficultyDescTasksComparator();
    public static final Comparator<Task> DATE_ASC_TASKS_COMPARATOR = new DateAscTasksComparator();
    public static final Comparator<Task> DATE_DESC_TASKS_COMPARATOR = new DateDescTasksComparator();

    public Task(String title, UUID id, int repeatability, int difficulty, int importance,
                Date date, boolean notify, Skill ... skills) {
        this(title, id, repeatability, difficulty, importance, date, notify, Arrays.asList(skills));
    }

    public Task(String title, UUID id, int repeatability, int difficulty, int importance,
                Date date, boolean notify, List<Skill> skills) {
        this.title = title;
        this.repeatability = repeatability;
        this.relatedSkills = skills;
        this.difficulty = difficulty;
        this.importance = importance;
        this.id = id;
        this.date = date;
        this.notify = notify;
    }

    public String getTitle() {
        return title;
    }

    public List<Skill> getRelatedSkills() {
        Collections.sort(relatedSkills, Skill.LEVEL_COMPARATOR);
        return relatedSkills;
    }

    public String getRelatedSkillsString() {
        Collections.sort(relatedSkills, Skill.LEVEL_COMPARATOR);
        StringBuilder sb = new StringBuilder();
        for (Skill sk : relatedSkills) {
            sb.append(sk.getId())
                    .append("::");
        }
        return sb.toString();
    }
    public void setRelatedSkills(List<Skill> relatedSkills) {
        this.relatedSkills = relatedSkills;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public int getRepeatability() {
        return repeatability;
    }

    public void setRepeatability(int repeatability) {
        this.repeatability = repeatability;
    }

    public boolean isTaskDone(){
        return repeatability == 0;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isUndonable() {
        return undonable;
    }

    public void setUndonable(boolean undonable) {
        this.undonable = undonable;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public void increaseDateByNDays(int N){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, N);
        date.setTime(cal.getTimeInMillis());
    }

    public double getMultiplier(){
        return (1 + (0.25 * difficulty) + (0.25 * importance));
    }

    public double getShareMultiplier(){
        return getMultiplier() * 0.5d;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task)) return false;
        else return this.id.equals(((Task) o).id);
    }

    public static String getDateFormatting() {
        return "EE, dd MMM, yyyy";
    }

    public static String getTimeFormatting() {
        return "kk:mm";
    }

    public static class SortingOrder{
        public static final int COMPLETION = 0;
        public static final int TITLE_ASC = 1;
        public static final int TITLE_DESC = 2;
        public static final int IMPORTANCE_ASC = 3;
        public static final int IMPORTANCE_DESC = 4;
        public static final int DIFFICULTY_ASC = 5;
        public static final int DIFFICULTY_DESC = 6;
        public static final int DATE_ASC = 7;
        public static final int DATE_DESC = 8;
    }

    public static class DateMode {
        public static final int TERMLESS = 0;
        public static final int WHOLE_DAY = 1;
        public static final int SPECIFIC_TIME = 2;
    }

    public static class RepeatMode {
        public static final int EVERY_NTH_DAY = 0;
        public static final int EVERY_NTH_MONTH = 1;
        public static final int EVERY_NTH_YEAR = 2;
        public static final int DAYS_OF_NTH_WEEK = 3;
        public static final int DO_NOT_REPEAT = 4;
    }

    private static class CompletionTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability){
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
                if (lhs.repeatability < 0) return -1;
                if (rhs.repeatability < 0) return 1;
                return rhs.repeatability - lhs.repeatability;
            }
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }

    private static class TitleAscTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability) {
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
            }
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }

    private static class TitleDescTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability) {
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class ImportanceAscTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability) {
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
            }
            if (lhs.getImportance() != rhs.getImportance()){
                return lhs.getImportance() - rhs.getImportance();
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class ImportanceDescTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability) {
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
            }
            if (lhs.getImportance() != rhs.getImportance()){
                return rhs.getImportance() - lhs.getImportance();
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class DifficultyAscTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability) {
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
            }
            if (lhs.getDifficulty() != rhs.getDifficulty()){
                return lhs.getDifficulty() - rhs.getDifficulty();
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class DifficultyDescTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability) {
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
            }
            if (lhs.getDifficulty() != rhs.getDifficulty()){
                return rhs.getDifficulty() - lhs.getDifficulty();
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class DateAscTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.getDate() != rhs.getDate()){
                if (lhs.repeatability == 0 && rhs.repeatability == 0){
                    return lhs.getDate().compareTo(rhs.getDate());
                }
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
                return lhs.getDate().compareTo(rhs.getDate());
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class DateDescTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.getDate() != rhs.getDate()){
                if (lhs.repeatability == 0 && rhs.repeatability == 0){
                    return rhs.getDate().compareTo(lhs.getDate());
                }
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
                return rhs.getDate().compareTo(lhs.getDate());
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }
}
