package com.levor.liferpgtasks.model;

import com.levor.liferpgtasks.Utils.TimeUnitUtils;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Task {
    public final static int LOW = 0;
    public final static int MEDIUM = 1;
    public final static int HIGH = 2;
    public final static int INSANE = 3;

    private String title;
    private Map<Skill, Boolean> relatedSkills = new TreeMap<>();
    private UUID id;
    private int repeatability = -1;
    private int repeatMode = RepeatMode.DO_NOT_REPEAT;
    private Boolean[] repeatDaysOfWeek = new Boolean[7];
    private int repeatIndex = 1;
    private int difficulty = LOW;
    private int importance = LOW;
    private int fear = LOW;
    private Date date;
    private Date finishDate;    //used for finished tasks that can be undone
    private int dateMode = DateMode.TERMLESS;
    private boolean undonable = false;
    private long notifyDelta = 24 * TimeUnitUtils.HOUR;
    private int habitDays = -1;
    private int habitDaysLeft = -1;
    private int numberOfExecutions = 0;
    private LocalDate habitStartDate = new LocalDate();
    private double moneyReward;

    private boolean updateNeeded = false;

    public static final Comparator<Task> COMPLETION_TASKS_COMPARATOR = new CompletionTasksComparator();
    public static final Comparator<Task> TITLE_ASC_TASKS_COMPARATOR = new TitleAscTasksComparator();
    public static final Comparator<Task> TITLE_DESC_TASKS_COMPARATOR = new TitleDescTasksComparator();
    public static final Comparator<Task> IMPORTANCE_ASC_TASKS_COMPARATOR = new ImportanceAscTasksComparator();
    public static final Comparator<Task> IMPORTANCE_DESC_TASKS_COMPARATOR = new ImportanceDescTasksComparator();
    public static final Comparator<Task> DIFFICULTY_ASC_TASKS_COMPARATOR = new DifficultyAscTasksComparator();
    public static final Comparator<Task> DIFFICULTY_DESC_TASKS_COMPARATOR = new DifficultyDescTasksComparator();
    public static final Comparator<Task> FEAR_ASC_TASKS_COMPARATOR = new FearAscTasksComparator();
    public static final Comparator<Task> FEAR_DESC_TASKS_COMPARATOR = new FearDescTasksComparator();
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

    private void removeNullsFromRelatedSkills() {
        List<Skill> skillsToRemove = new ArrayList<>();
        for(Map.Entry<Skill, Boolean> pair : relatedSkills.entrySet()) {
            if (pair.getKey() == (null)) {
                skillsToRemove.add(pair.getKey());
            }
        }
        for (Skill sk: skillsToRemove) {
            relatedSkills.remove(sk);
        }
    }

    public List<Skill> getRelatedSkillsList() {
        removeNullsFromRelatedSkills();
        List<Skill> skillsList = new ArrayList<>();
        for (Map.Entry<Skill, Boolean> pair : relatedSkills.entrySet()) {
            skillsList.add(pair.getKey());
        }
        Collections.sort(skillsList, Skill.LEVEL_COMPARATOR);
        return skillsList;
    }

    public Map<Skill, Boolean> getRelatedSkillsMap() {
        return relatedSkills;
    }

    public String getRelatedSkillsString() {
        removeNullsFromRelatedSkills();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Skill, Boolean> pair : relatedSkills.entrySet()) {
            Skill sk = pair.getKey();
            boolean increaseSkill = pair.getValue();
            if (sk == null) continue;
            sb.append(sk.getId())
                    .append(":;")
                    .append(increaseSkill ? "+" : "-")
                    .append("::");
        }
        return sb.toString();
    }

    public void removeAllRelatedSkills() {
        relatedSkills = new TreeMap<>();
    }

    public void setRelatedSkills(Map<Skill, Boolean> relatedSkills) {
        this.relatedSkills = relatedSkills;
    }

    public void addRelatedSkill(Skill skill, Boolean increaseSkill) {
        relatedSkills.put(skill, increaseSkill);
    }

    public void removeRelatedSkill(Skill skill) {
        relatedSkills.remove(skill);
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

    public int getFear() {
        return fear;
    }

    public void setFear(int fear) {
        this.fear = fear;
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

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public boolean isUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
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

    public int getNumberOfExecutions() {
        return numberOfExecutions;
    }

    public void setNumberOfExecutions(int numberOfExecutions) {
        this.numberOfExecutions = numberOfExecutions;
    }

    public double getMoneyReward() {
        return moneyReward;
    }

    public void setMoneyReward(double moneyReward) {
        this.moneyReward = moneyReward;
    }

    public double getMultiplier(){
        return getMultiplierByFormula(difficulty, importance, fear);
    }

    public static double getMultiplierByFormula(int difficulty, int importance, int fear) {
        float m1 = difficulty / 100f;
        float m2 = importance / 100f;
        float m3 = fear / 100f;
        return (m1 + m2 + m3 + 2*m1*m2 + 2*m1*m3 + 2*m2*m3 + 3*m1*m2*m3);
    }

    public double getShareMultiplier(){
        return getMultiplier() * 0.5d;
    }

    public boolean isContainsDecresingSkills() {
        return relatedSkills.values().contains(false);
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
        numberOfExecutions++;
        moveToNextRepeatDate();
    }

    public void skip() {
        moveToNextRepeatDate();
    }

    private void moveToNextRepeatDate() {
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
                case RepeatMode.REPEAT_AFTER_COMPLETION:
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTime(new Date());
                    if (dateMode == DateMode.TERMLESS) {
                        dateMode = DateMode.WHOLE_DAY;
                    } else if (dateMode == DateMode.SPECIFIC_TIME) {
                        tempCal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                        tempCal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                    }
                    cal = tempCal;
                    cal.add(Calendar.DAY_OF_YEAR, repeatIndex);
                    break;
                default : //not repeat and simple repeat
                    //do not change date
                    break;
            }
            date = cal.getTime();
        }
    }

    public void undo() {
        numberOfExecutions--;
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
                case RepeatMode.REPEAT_AFTER_COMPLETION:
                    cal.add(Calendar.DAY_OF_YEAR, -repeatIndex);
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

    public void reset() {
        numberOfExecutions = 0;
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
        public static final int FEAR_ASC = 9;
        public static final int FEAR_DESC = 10;
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
        public static final int REPEAT_AFTER_COMPLETION = 6;
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

    private static class FearAscTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability) {
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
            }
            if (lhs.getFear() != rhs.getFear()){
                return lhs.getFear() - rhs.getFear();
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class FearDescTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability) {
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
            }
            if (lhs.getFear() != rhs.getFear()){
                return rhs.getFear() - lhs.getFear();
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class DateAscTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            Date dateLhs = lhs.isTaskDone() ? lhs.getFinishDate() : lhs.getDate();
            Date dateRhs = rhs.isTaskDone() ? rhs.getFinishDate() : rhs.getDate();
            if (dateLhs != dateRhs && dateLhs != null && dateRhs != null){
                if (lhs.repeatability == 0 && rhs.repeatability == 0){
                    return dateLhs.compareTo(dateRhs);
                }
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
                return dateLhs.compareTo(dateRhs);
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }

    private static class DateDescTasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            Date dateLhs = lhs.isTaskDone() ? lhs.getFinishDate() : lhs.getDate();
            Date dateRhs = rhs.isTaskDone() ? rhs.getFinishDate() : rhs.getDate();
            if (dateLhs != dateRhs && dateLhs != null && dateRhs != null){
                if (lhs.repeatability == 0 && rhs.repeatability == 0){
                    return dateRhs.compareTo(dateLhs);
                }
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
                return dateRhs.compareTo(dateLhs);
            }
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }
}
