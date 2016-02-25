package com.levor.liferpgtasks.model;

import com.levor.liferpgtasks.Utils.TimeUnitUtils;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Task {
    public final static int LOW = 0;
    public final static int MEDIUM = 1;
    public final static int HIGH = 2;
    public final static int INSANE = 3;

    private String title;
    private List<Skill> relatedSkills = new ArrayList<>();
    private UUID id;
    private int repeatability = -1;
    private int repeatMode = RepeatMode.DO_NOT_REPEAT;
    private Boolean[] repeatDaysOfWeek = new Boolean[7];
    private int repeatIndex = 1;
    private int difficulty = LOW;
    private int importance = LOW;
    private Date date;
    private int dateMode = DateMode.TERMLESS;
    private boolean undonable = false;
    private long notifyDelta = 24 * TimeUnitUtils.HOUR;
    private int habitDays = -1;
    private int habitDaysLeft = -1;
    private LocalDate habitStartDate = new LocalDate();

    public static final Comparator<Task> COMPLETION_TASKS_COMPARATOR = new CompletionTasksComparator();
    public static final Comparator<Task> TITLE_ASC_TASKS_COMPARATOR = new TitleAscTasksComparator();
    public static final Comparator<Task> TITLE_DESC_TASKS_COMPARATOR = new TitleDescTasksComparator();
    public static final Comparator<Task> IMPORTANCE_ASC_TASKS_COMPARATOR = new ImportanceAscTasksComparator();
    public static final Comparator<Task> IMPORTANCE_DESC_TASKS_COMPARATOR = new ImportanceDescTasksComparator();
    public static final Comparator<Task> DIFFICULTY_ASC_TASKS_COMPARATOR = new DifficultyAscTasksComparator();
    public static final Comparator<Task> DIFFICULTY_DESC_TASKS_COMPARATOR = new DifficultyDescTasksComparator();
    public static final Comparator<Task> DATE_ASC_TASKS_COMPARATOR = new DateAscTasksComparator();
    public static final Comparator<Task> DATE_DESC_TASKS_COMPARATOR = new DateDescTasksComparator();

    public Task (String title){
        this.title = title;
        this.id = UUID.randomUUID();
        for (int i = 0; i < repeatDaysOfWeek.length; i++) {
            repeatDaysOfWeek[i] = false;
        }
    }

    public Task (String title, UUID id){
        this.title = title;
        this.id = id;
        for (int i = 0; i < repeatDaysOfWeek.length; i++) {
            repeatDaysOfWeek[i] = false;
        }
    }

    public String getTitle() {
        return title;
    }

    public List<Skill> getRelatedSkills() {
        relatedSkills.removeAll(Collections.singleton(null));
        Collections.sort(relatedSkills, Skill.LEVEL_COMPARATOR);
        return relatedSkills;
    }

    public String getRelatedSkillsString() {
        relatedSkills.removeAll(Collections.singleton(null));
        Collections.sort(relatedSkills, Skill.LEVEL_COMPARATOR);
        StringBuilder sb = new StringBuilder();
        for (Skill sk : relatedSkills) {
            if (sk == null) continue;
            sb.append(sk.getId())
                    .append("::");
        }
        return sb.toString();
    }
    public void setRelatedSkills(List<Skill> relatedSkills) {
        this.relatedSkills = relatedSkills;
    }

    public void addRelatedSkill(Skill skill) {
        relatedSkills.add(skill);
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

    public Date getNotificationDate() {
        return new Date(date.getTime() - notifyDelta);
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

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public Boolean[] getRepeatDaysOfWeek() {
        return repeatDaysOfWeek;
    }

    public String getRepeatDaysOfWeekString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeatDaysOfWeek.length; i++) {
            sb.append(repeatDaysOfWeek[i] ? 1 : 0);
        }
        return sb.toString();
    }

    public void setRepeatDaysOfWeekFromString(String days) {
        if (days != null)
            for (int i = 0; i < repeatDaysOfWeek.length; i++) {
                repeatDaysOfWeek[i] = Integer.parseInt(("" + days.charAt(i))) == 1;
            }
    }

    public void setRepeatDaysOfWeek(Boolean[] repeatDaysOfWeek) {
        if (repeatDaysOfWeek == null) {
            for (int i = 0; i < this.repeatDaysOfWeek.length; i++) {
                this.repeatDaysOfWeek[i] = false;
            }
        } else {
            this.repeatDaysOfWeek = repeatDaysOfWeek;
        }
    }

    public int getRepeatIndex() {
        return repeatIndex;
    }

    public void setRepeatIndex(int repeatIndex) {
        this.repeatIndex = repeatIndex;
    }

    public int getDateMode() {
        return dateMode;
    }

    public void setDateMode(int dateMode) {
        this.dateMode = dateMode;
    }

    public long getNotifyDelta() {
        return notifyDelta;
    }

    public void setNotifyDelta(long notifyDelta) {
        this.notifyDelta = notifyDelta;
    }

    public int getHabitDays() {
        return habitDays;
    }

    public void setHabitDays(int habitDays) {
        if (habitDays == 0) habitDays = -1;
        this.habitDays = habitDays;
    }

    public int getHabitDaysLeft() {
        return habitDaysLeft;
    }

    public void setHabitDaysLeft(int habitDaysLeft) {
        if (habitDaysLeft == 0) habitDaysLeft = -1;
        this.habitDaysLeft = habitDaysLeft;
    }

    public LocalDate getHabitStartDate() {
        return habitStartDate;
    }

    public void setHabitStartDate(LocalDate habitStartDate) {
        this.habitStartDate = habitStartDate;
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

    public void perform(){
        setRepeatability(getRepeatability() - 1);
        if (repeatability != 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            switch (repeatMode) {
                case RepeatMode.EVERY_NTH_DAY :
                    cal.add(Calendar.DAY_OF_YEAR, repeatIndex);
                    break;
                case RepeatMode.EVERY_NTH_MONTH :
                    cal.add(Calendar.MONTH, repeatIndex);
                    break;
                case RepeatMode.EVERY_NTH_YEAR :
                    cal.add(Calendar.YEAR, repeatIndex);
                    break;
                case RepeatMode.DAYS_OF_NTH_WEEK :
                    int week = cal.get(Calendar.WEEK_OF_YEAR);
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    for (int i = 0; i < getRepeatDaysOfWeek().length; i++) {
                        if (getRepeatDaysOfWeek()[cal.get(Calendar.DAY_OF_WEEK) - 1]){
                            break;
                        } else {
                            cal.add(Calendar.DAY_OF_YEAR, 1);
                        }
                    }

                    int newWeek = cal.get(Calendar.WEEK_OF_YEAR);
                    if (week != newWeek) {
                        cal.add(Calendar.WEEK_OF_YEAR, repeatIndex - 1);
                    }

                    break;
                default : //not repeat and simple repeat
                    //do not change date
                    break;
            }
            date = cal.getTime();
        }
    }

    public void undo() {
        if (repeatability != 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            switch (repeatMode) {
                case RepeatMode.EVERY_NTH_DAY:
                    cal.add(Calendar.DAY_OF_YEAR, -repeatIndex);
                    break;
                case RepeatMode.EVERY_NTH_MONTH:
                    cal.add(Calendar.MONTH, -repeatIndex);
                    break;
                case RepeatMode.EVERY_NTH_YEAR:
                    cal.add(Calendar.YEAR, -repeatIndex);
                    break;
                case RepeatMode.DAYS_OF_NTH_WEEK:
                    int week = cal.get(Calendar.WEEK_OF_YEAR);
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    for (int i = 0; i < getRepeatDaysOfWeek().length; i++) {
                        if (getRepeatDaysOfWeek()[cal.get(Calendar.DAY_OF_WEEK) - 1]) {
                            break;
                        } else {
                            cal.add(Calendar.DAY_OF_YEAR, -1);
                        }
                    }
                    int newWeek = cal.get(Calendar.WEEK_OF_YEAR);
                    if (week != newWeek) {
                        cal.add(Calendar.WEEK_OF_YEAR, -(repeatIndex - 1));
                    }
                    break;
                default: //not repeat and simple repeat
                    //do not change date
                    break;
            }
            date = cal.getTime();
        }

        if (getRepeatability() >= 0) {
            setRepeatability(getRepeatability() + 1);
        }
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
        public static final int SIMPLE_REPEAT = 5;
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
