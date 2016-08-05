package com.levor.liferpgtasks.controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.levor.liferpgtasks.AchievsList;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.TimeUnitUtils;
import com.levor.liferpgtasks.broadcastReceivers.TaskNotification;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Hero;
import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Misc;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.levor.liferpgtasks.view.fragments.settings.DonationFragment;
import com.levor.liferpgtasks.widget.LifeRPGWidgetProvider;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.levor.liferpgtasks.AchievsList.*;

public class LifeController {
    public static final String FIRTS_RUN_TAG = "first_run_ tag";
    public static final String TASK_TITLE_NOTIFICATION_TAG = "task_id_notification_ tag";
    public static final String TASK_ID_NOTIFICATION_TAG = "id_notification_ tag";
    public static final String SHARED_PREFS_TAG = "shared_prefs_tag";
    public static final String PERFORMED_TASKS_TAG = "performed_task_tag";
    public static final String TOTAL_TASKS_NUMBER_TAG = "total_tasks_number_tag";
    public static final String FINISHED_TASKS_NUMBER_TAG = "finished_tasks_number_tag";
    public static final String TOTAL_HERO_XP_TAG = "total_hero_xp_tag";
    public static final String TOTAL_SKILLS_XP_TAG = "total_skills_xp_tag";
    public static final String XP_MULTIPLIER_TAG = "xp_multiplier_tag";
    public static final String ACHIEVEMENTS_COUNT_TAG = "achievements_count_tag";
    public static final String ACHIEVEMENTS_TAG = "achievements_tag";
    public static final String DROPBOX_AUTO_BACKUP_ENABLED = "dropbox_auto_backup_enabled";
    public final static String DROPBOX_ACCESS_TOKEN_TAG = "db_access_token_tag";
    public final static String APPLICATION_VERSION_CODE_TAG = "application_version_code_tag";
    public static final String SHOW_ONLY_TODAY_TASK_TAG = "show_only_today_tasks_tag";
    public static final String SHOW_DAILIES_IN_DONE_TAG = "show_dailies_in_done_tag";
    private static final String HERO_ICON_NAME_TAG = "hero_icon_name_tag";
    public static final String FILE_EXPORT_PATH = Environment.getExternalStorageDirectory().getPath()
            +"/LifeRGPTasks/";
    public static final String DB_EXPORT_FILE_NAME = FILE_EXPORT_PATH + "LifeRPGTasksDB.db";
    public static final String HERO_IMAGE_FILE_NAME = FILE_EXPORT_PATH + "HeroPhoto.jpg";

    public static final int CAMERA_CAPTURE_REQUEST = 1001;
    public static final int SELECT_FILE_IN_FILESYSTEM_REQUEST = 1002;

    private LifeEntity lifeEntity;
    private Context context;
    private Tracker tracker;
    private MainActivity currentActivity;
    private List<Integer> achievementsLevels = new ArrayList<>();
    private Map<String, Float> statisticsNumbers = new LinkedHashMap<>();

    private static final String STAT_DIVIDER = " - ";

    private long dropboxBackupTimeout = 5000;
    private long dropboxBackupStartTime;

    private static LifeController LifeController;
    public static LifeController getInstance(Context context){
        if (LifeController == null){
            LifeController = new LifeController(context);
        }
        return LifeController;
    }

    private LifeController(Context context) {
        lifeEntity = LifeEntity.getInstance(context);
        this.context = context;
        initAchievements();
        initStatistics();
    }

    public void setGATracker(Tracker tracker){
        this.tracker = tracker;
    }

    public Tracker getGATracker() {
        return tracker;
    }

    public void setCurrentActivity(MainActivity activity){
        this.currentActivity = activity;
    }

    public void sendScreenNameToAnalytics(String name){
        tracker.setScreenName("/" + name);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public List<Task> getAllTasks(){
        return lifeEntity.getTasks();
    }

    public List<String> getSkillsTitles(){
        return lifeEntity.getSkillsTitles();
    }

    public List<Skill> getAllSkills(){
        return lifeEntity.getSkills();
    }

    public Task getTaskByTitle(String s) {
        return lifeEntity.getTaskByTitle(s);
    }

    public void createNewTask(Task task) {
        lifeEntity.addTask(task);
        updateHomeScreenWidgets();
        updateStatistics(TOTAL_TASKS_NUMBER_TAG, 1);
        checkAchievements();
        performBackUpToDropBox();
    }

    public void updateTask(Task task) {
        lifeEntity.updateTask(task);
        updateHomeScreenWidgets();
        performBackUpToDropBox();
    }

    public void addSkill(String title, List<Characteristic> keyCharList){
        lifeEntity.addSkill(title, keyCharList);
        getGATracker().send(new HitBuilders.EventBuilder()
                .setCategory(context.getString(R.string.GA_action))
                .setAction("New skill: " + title)
                .build());
        performBackUpToDropBox();
    }

    public Skill getSkillByTitle(String title) {
        return lifeEntity.getSkillByTitle(title);
    }

    public List<Task> getTasksBySkill(Skill sk){
        return lifeEntity.getTasksBySkill(sk);
    }

    public void removeTask(Task task) {
        lifeEntity.removeTask(task);
        updateHomeScreenWidgets();
        removeTaskNotification(task);
        performBackUpToDropBox();
    }
    public String[] getCharacteristicsTitleAndLevelAsArray(){
        List<Characteristic> characteristics = lifeEntity.getCharacteristics();
        ArrayList<String> strings = new ArrayList<>();
        for (Characteristic ch : characteristics){
            strings.add(ch.getTitle() + " - " + ch.getLevel());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public List<Characteristic> getCharacteristics(){
        return lifeEntity.getCharacteristics();
    }

    public Characteristic getCharacteristicByTitle(String title) {
        return lifeEntity.getCharacteristicByTitle(title);
    }

    public Characteristic getCharacteristicByID(UUID id) {
        return lifeEntity.getCharacteristicById(id);
    }

    public ArrayList<Skill> getSkillsByCharacteristic(Characteristic ch) {
        return lifeEntity.getSkillsByCharacteristic(ch);
    }

    public void updateCharacteristic(Characteristic ch) {
        lifeEntity.updateCharacteristic(ch);
    }

    public Task getTaskByID(UUID id) {
        return lifeEntity.getTaskByID(id);
    }

    public Skill getSkillByID(UUID id) {
        return lifeEntity.getSkillByID(id);
    }

    public void removeSkill(Skill skill) {
        List<Task> tasks = getTasksBySkill(skill);
        for (Task t : tasks){
            t.removeRelatedSkill(skill);
        }
        lifeEntity.removeSkill(skill);
        performBackUpToDropBox();
    }

    public void removeCharacteristic(Characteristic ch) {
        List<Skill> affectedSkils = getSkillsByCharacteristic(ch);
        for (Skill sk : affectedSkils) {
            sk.removeKeyCharacteristic(ch);
        }
        lifeEntity.removeCharacteristic(ch);
    }

    public void addCharacteristic(Characteristic characteristic) {
        lifeEntity.addCharacteristic(characteristic);
        getGATracker().send(new HitBuilders.EventBuilder()
                .setCategory(context.getString(R.string.GA_action))
                .setAction("Added characteristic: " + characteristic.getTitle())
                .setValue(1)
                .build());
    }

    public boolean performTask(Task task){
        Hero hero = lifeEntity.getHero();
        task.setUndonable(true);
        task.perform();
        if (task.getRepeatability() <= 0) {
            task.setFinishDate(new Date());
            if (task.getRepeatability() == 0) {
                updateStatistics(FINISHED_TASKS_NUMBER_TAG, 1);
            }
        }
        updateTask(task);
        updateTaskNotification(task);
        double multiplier = task.getMultiplier();
        double finalXP = hero.getBaseXP() * multiplier;
        int decreasingSkillsCount = 0;
        int increasingSkillsCount = 0;
        for (Map.Entry<Skill, Boolean> pair : task.getRelatedSkillsMap().entrySet()) {
            Skill sk = pair.getKey();
            boolean increaseSkill = pair.getValue();
            if (!increaseSkill) {
                decreasingSkillsCount++;
            } else {
                increasingSkillsCount++;
            }
            if (sk == null) continue;
            boolean skillChanged = increaseSkill ? sk.increaseSublevel(finalXP) : sk.decreaseSublevel(finalXP);
            if (skillChanged){
                for (Characteristic ch : sk.getKeyCharacteristicsList()) {
                    updateCharacteristic(ch);
                }
            }
            updateSkill(sk);
            updateStatistics(TOTAL_SKILLS_XP_TAG, (float) (increaseSkill ? finalXP : -finalXP));
        }
        if (decreasingSkillsCount > increasingSkillsCount) {
            finalXP = - finalXP;
        }
        boolean isLevelIncreased = hero.addXP(finalXP);
        lifeEntity.updateHero(hero);
        updateStatistics(TOTAL_HERO_XP_TAG, (float) finalXP);
        checkTaskHabitGeneration(task);
        increaseTasksPerDay(1);

        //GA
        if (isLevelIncreased){
            getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(context.getString(R.string.GA_action))
                    .setAction(context.getString(R.string.GA_hero_level_increased) + " " + hero.getLevel())
                    .build());
        }

        if (isInternetConnectionActive()){
            getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(context.getString(R.string.GA_action))
                    .setAction(context.getString(R.string.GA_task_performed_internet))
                    .setValue(1)
                    .build());
            updateStatistics(PERFORMED_TASKS_TAG, 1);
        } else {
            getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(context.getString(R.string.GA_action))
                    .setAction(context.getString(R.string.GA_task_performed))
                    .setValue(1)
                    .build());
            updateStatistics(PERFORMED_TASKS_TAG, 1);
        }
        if (task.getRepeatability() == 0){
            getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(context.getString(R.string.GA_action))
                    .setAction(context.getString(R.string.GA_task_finished))
                    .build());
        }
        ///GA

        performBackUpToDropBox();
        checkAchievements();
        return isLevelIncreased;
    }

    public boolean undoTask(Task task){
        Hero hero = lifeEntity.getHero();
        task.setUndonable(false);
        task.undo();
        task.setFinishDate(null);
        updateTask(task);
        updateTaskNotification(task);
        double multiplier = task.getMultiplier();
        double finalXP = hero.getBaseXP() * multiplier;
        int decreasingSkillsCount = 0;
        int increasingSkillsCount = 0;
        for (Map.Entry<Skill, Boolean> pair : task.getRelatedSkillsMap().entrySet()) {
            Skill sk = pair.getKey();
            boolean increaseSkill = pair.getValue();
            if (!increaseSkill) {
                decreasingSkillsCount++;
            } else {
                increasingSkillsCount++;
            }
            if (sk == null) continue;
            boolean skillChanged = increaseSkill ? sk.decreaseSublevel(finalXP) : sk.increaseSublevel(finalXP);
            if (skillChanged){
                for (Characteristic ch : sk.getKeyCharacteristicsList()) {
                    updateCharacteristic(ch);
                }
            }
            updateSkill(sk);
            updateStatistics(TOTAL_SKILLS_XP_TAG, (float) (increaseSkill ? -finalXP : finalXP));
        }
        if (decreasingSkillsCount > increasingSkillsCount) {
            finalXP = - finalXP;
        }
        boolean isLevelChanged = hero.addXP(-finalXP);
        lifeEntity.updateHero(hero);
        updateStatistics(TOTAL_HERO_XP_TAG, (float) -finalXP);
        updateStatistics(PERFORMED_TASKS_TAG, -1);
        checkTaskHabitGeneration(task);
        increaseTasksPerDay(-1);
        checkAchievements();
//        performBackUpToDropBox();
        return isLevelChanged;
    }

    public void skipTask(Task task) {
        task.skip();
        task.setUndonable(false);

        if (task.getHabitDays() >= 0) {
            LocalDate today = new LocalDate();
            task.setHabitStartDate(today.minusDays(1));
            task.setHabitDaysLeft(task.getHabitDays());
        }

        updateTask(task);
        updateTaskNotification(task);
    }

    public boolean shareTask(Task task){
        Hero hero = lifeEntity.getHero();
        double multiplier = task.getShareMultiplier();
        double finalXP = hero.getBaseXP() * multiplier;
        boolean isLevelIncreased = hero.addXP(finalXP);
        lifeEntity.updateHero(hero);
        updateStatistics(TOTAL_HERO_XP_TAG, (float) finalXP);

        if (isLevelIncreased){
            getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(context.getString(R.string.GA_action))
                    .setAction(context.getString(R.string.GA_hero_level_increased) + " " + hero.getLevel())
                    .build());
        }
        checkAchievements();
        performBackUpToDropBox();
        return  isLevelIncreased;
    }

    public void updateSkill(Skill skill) {
        lifeEntity.updateSkill(skill);
        performBackUpToDropBox();
    }

    public Hero getHero(){
        return lifeEntity.getHero();
    }

    public String getHeroName(){
        return lifeEntity.getHero().getName();
    }

    public int getHeroLevel(){
        return lifeEntity.getHero().getLevel();
    }

    public double getHeroXp(){
        return lifeEntity.getHero().getXp();
    }

    public double getHeroXpToNextLevel(){
        return lifeEntity.getHero().getXpToNextLevel();
    }

    public void updateHeroName(String name){
        Hero hero = lifeEntity.getHero();
        hero.setName(name);
        lifeEntity.updateHero(hero);
        performBackUpToDropBox();
    }

    public void checkHabitGenerationForAllTasks(){
        List<Task> tasks = getAllTasks();
        boolean[] updateTasks = new boolean[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (t.getRepeatability() < 0 && t.getHabitDays() > 0) {
                boolean update = checkTaskHabitGeneration(t);
                updateTasks[i] = update;
            }
        }
        for (int i = 0; i < tasks.size(); i++) {
            if (updateTasks[i]) {
                lifeEntity.updateTask(tasks.get(i));
            }
        }
    }

    public boolean checkTaskHabitGeneration(Task t) {
        if (t.getHabitDays() < 1) return false;
        LocalDate nextRepeatDate = LocalDate.fromDateFields(t.getDate());
        LocalDate today = new LocalDate();
        LocalDate habitStartDate = t.getHabitStartDate();

        if (nextRepeatDate.isBefore(today)) {
            t.setHabitStartDate(today.minusDays(1));
            t.setHabitDaysLeft(t.getHabitDays());
            Toast.makeText(currentActivity, currentActivity.getString(R.string.habit_generation_failed,t.getTitle()),
                    Toast.LENGTH_LONG)
                    .show();
            return true;
        } else if (Days.daysBetween(today, nextRepeatDate).getDays() == 0) {
            if (today.equals(habitStartDate)) return false;
            int diff = Math.abs(Days.daysBetween(today.minusDays(1), habitStartDate).getDays());
            t.setHabitDaysLeft(t.getHabitDays() - diff);
        } else {
            int diff = Math.abs(Days.daysBetween(today, habitStartDate).getDays());
            t.setHabitDaysLeft(t.getHabitDays() - diff);
        }
        if (t.getHabitDaysLeft() < 0) {
            t.setHabitDaysLeft(-1);
            t.setHabitDays(-1);
            Toast.makeText(currentActivity, currentActivity.getString(R.string.habit_generation_finished,t.getTitle()),
                    Toast.LENGTH_LONG)
                    .show();
            lifeEntity.updateTask(t);
            return true;
        }
        return false;
    }

    public void setupTasksNotifications(){
        // remove all previous notifications
//        NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
//        nManager.cancelAll();

        for (Task t: getAllTasks()){
            updateTaskNotification(t);
        }
    }

    public void updateTaskNotification(Task task){
        removeTaskNotification(task);
        addTaskNotification(task);
    }

    public void addTaskNotification(Task task){
        if (task.getNotifyDelta() < 0) return;
        Date currentDate = new Date(System.currentTimeMillis());
        Date notifyDate = task.getNotificationDate();
        if (notifyDate.before(currentDate)) return;
        Intent intent = new Intent(context, TaskNotification.class);
        intent.putExtra(TASK_TITLE_NOTIFICATION_TAG, task.getTitle());
        intent.putExtra(TASK_ID_NOTIFICATION_TAG, task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                task.getId().hashCode(), intent, 0);
        Calendar cal = Calendar.getInstance();
        cal.setTime(notifyDate);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        long repeatTime;
        if (task.getRepeatMode() == Task.RepeatMode.EVERY_NTH_DAY){
            repeatTime = TimeUnitUtils.DAY * task.getRepeatIndex();
        } else if (task.getRepeatMode() == Task.RepeatMode.EVERY_NTH_MONTH){
            cal.setTime(notifyDate);
            cal.add(Calendar.MONTH, 1);
            repeatTime = (cal.getTime().getTime() - notifyDate.getTime()) * task.getRepeatIndex();
        } else if (task.getRepeatMode() == Task.RepeatMode.EVERY_NTH_YEAR){
            cal.setTime(notifyDate);
            cal.add(Calendar.YEAR, 1);
            repeatTime = (cal.getTime().getTime() - notifyDate.getTime()) * task.getRepeatIndex();
        } else if (task.getRepeatMode() == Task.RepeatMode.DAYS_OF_NTH_WEEK){
            cal.setTime(notifyDate);
            int week = cal.get(Calendar.WEEK_OF_YEAR);
            for (int i = 0; i < task.getRepeatDaysOfWeek().length; i++) {
                if (task.getRepeatDaysOfWeek()[cal.get(Calendar.DAY_OF_WEEK) - 1]){
                    break;
                } else {
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                }
            }
            int newWeek = cal.get(Calendar.WEEK_OF_YEAR);
            if (week != newWeek) {
                cal.add(Calendar.WEEK_OF_YEAR, task.getRepeatIndex() - 1);
            }
            repeatTime = cal.getTime().getTime() - notifyDate.getTime();
        } else { //no notification needed for this modes
            return;
        }
        if (task.getRepeatability() != 0) {
            alarmManager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), repeatTime, pendingIntent);
        }
    }

    public void removeTaskNotification(Task task){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        Intent i = new Intent(context, TaskNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId().hashCode(), i, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public boolean isInternetConnectionActive(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void performVKLogin(Activity activity){
        if (!VKSdk.isLoggedIn()){
            VKSdk.login(activity, VKScope.WALL);
        }
    }

    private void updateHomeScreenWidgets(){
        int ids[] = AppWidgetManager.getInstance(context).
                getAppWidgetIds(new ComponentName(context, LifeRPGWidgetProvider.class));
        for (int id : ids)
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(id, R.id.widget_list_view);
    }

    private void initStatistics(){
        if (Misc.STATISTICS_NUMBERS != null) {
            String[] numbersArray = Misc.STATISTICS_NUMBERS.split(STAT_DIVIDER);
            List<Float> numbers = new ArrayList<>();
            for (String aNumbersArray : numbersArray) {
                numbers.add(Float.parseFloat(aNumbersArray));
            }
            statisticsNumbers.put(PERFORMED_TASKS_TAG, numbers.get(0));
            statisticsNumbers.put(TOTAL_TASKS_NUMBER_TAG, numbers.get(1));
            statisticsNumbers.put(FINISHED_TASKS_NUMBER_TAG, numbers.get(2));
            statisticsNumbers.put(TOTAL_HERO_XP_TAG, numbers.get(3));
            statisticsNumbers.put(TOTAL_SKILLS_XP_TAG, numbers.get(4));
            statisticsNumbers.put(ACHIEVEMENTS_COUNT_TAG, numbers.get(5));
        } else {
            statisticsNumbers.put(PERFORMED_TASKS_TAG, 0f);
            statisticsNumbers.put(TOTAL_TASKS_NUMBER_TAG, 0f);
            statisticsNumbers.put(FINISHED_TASKS_NUMBER_TAG, 0f);
            statisticsNumbers.put(TOTAL_HERO_XP_TAG, 0f);
            statisticsNumbers.put(TOTAL_SKILLS_XP_TAG, 0f);
            statisticsNumbers.put(ACHIEVEMENTS_COUNT_TAG, 0f);
        }
        statisticsNumbers.put(XP_MULTIPLIER_TAG, (float) getHero().getBaseXP());

    }

    private void updateStatistics(String field, float value){
        switch (field) {
            case XP_MULTIPLIER_TAG :
                Hero hero = getHero();
                hero.setBaseXP(hero.getBaseXP() + value);
                lifeEntity.updateHero(hero);
                break;
            default:
                float prevValue = statisticsNumbers.get(field);
                prevValue += value;
                statisticsNumbers.put(field, prevValue);
                break;
        }
        performBackUpToDropBox();
    }

    public float getStatisticsValue(String field){
        switch (field) {
            case XP_MULTIPLIER_TAG:
                return (float)getHero().getBaseXP();
            default:
                return statisticsNumbers.get(field);
        }
    }

    private void updateStatisticsToMisc(){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Float> e : statisticsNumbers.entrySet()){
            sb.append(e.getValue()).append(STAT_DIVIDER);
        }
        sb.delete(sb.length() - 3, sb.length() - 1);
        Misc.STATISTICS_NUMBERS = sb.toString();
    }

    private void initAchievements(){
        TOTAL_HERO_XP.setDescription(context.getString(R.string.total_hero_xp_achievement));
        TOTAL_SKILLS_XP.setDescription(context.getString(R.string.total_skills_xp_achievement));
        PERFORMED_TASKS.setDescription(context.getString(R.string.performed_tasks_achievement));
        FINISHED_TASKS.setDescription(context.getString(R.string.finished_tasks_achievement));
        ADDED_TASKS.setDescription(context.getString(R.string.added_tasks_achievement));
        TOP_LEVEL_SKILL.setDescription(context.getString(R.string.top_level_skill_achievement));
        TOP_LEVEL_CHARACTERISTIC.setDescription(context.getString(R.string.top_level_characteristic_achievement));
        HERO_LEVEL.setDescription(context.getString(R.string.hero_level_achievement));
        NUMBER_OF_SKILLS_WITH_LEVEL_10.setDescription(context.getString(R.string.n_skills_level_10_achievement));
        NUMBER_OF_SKILLS_WITH_LEVEL_25.setDescription(context.getString(R.string.n_skills_level_25_achievement));
        NUMBER_OF_SKILLS_WITH_LEVEL_50.setDescription(context.getString(R.string.n_skills_level_50_achievement));
        NUMBER_OF_SKILLS_WITH_LEVEL_100.setDescription(context.getString(R.string.n_skills_level_100_achievement));


        String defaultString = Misc.ACHIEVEMENTS_LEVELS;
        achievementsLevels.clear();

        //moving from preferences to DB (from ver.2)
        if (defaultString == null) {
            SharedPreferences prefs = getSharedPreferences();
            defaultString = prefs.getString(ACHIEVEMENTS_TAG, null);
            String imagePath = prefs.getString(HERO_ICON_NAME_TAG, null);
            if (imagePath != null){
                Misc.HERO_IMAGE_PATH = imagePath;
                prefs.edit().putString(HERO_ICON_NAME_TAG, null).apply();
            }
        }

        if (defaultString == null) {
            for (int i = 0; i < AchievsList.values().length; i++) {
                achievementsLevels.add(0);
            }
        } else {
            String[] array = defaultString.split(",");
            for (String s : array) {
                achievementsLevels.add(Integer.parseInt(s));
            }
        }
    }

    private void updateAchievementsToMisc(){
        StringBuilder sb = new StringBuilder();
        for (Integer i : achievementsLevels){
            sb.append(i).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        Misc.ACHIEVEMENTS_LEVELS = sb.toString();
    }

    private void unlockAchievement(AchievsList achievement){
        int position = achievement.ordinal();
        String achievementString = String.format(achievement.getDescription(),
                achievement.getThresholdForLevel(achievementsLevels.get(position)));
        Toast.makeText(context,
                context.getString(R.string.achievement_unlocked, achievementString) + "\n" +
                        context.getString(R.string.xp_multiplier_reward, achievement.getReward()),
                Toast.LENGTH_LONG).show();

        achievementsLevels.set(position, achievementsLevels.get(position) + 1);
        Hero hero = getHero();
        hero.setBaseXP(hero.getBaseXP() + (achievement.getReward() * 0.01));
        lifeEntity.updateHero(hero);

        updateStatistics(ACHIEVEMENTS_COUNT_TAG, 1);
        getGATracker().send(new HitBuilders.EventBuilder()
                .setCategory(context.getString(R.string.GA_action))
                .setAction(context.getString(R.string.GA_achievement_unlocked) + " " + achievement.name())
                .setValue(1)
                .build());

        performBackUpToDropBox();
    }

    public List<Integer> getAchievementsLevels(){
        return achievementsLevels;
    }

    private void checkAchievements(){
        int skillsXPAchievePosition = achievementsLevels.get(TOTAL_HERO_XP.ordinal());
        if (getHero().getTotalXP() >= TOTAL_HERO_XP.getThresholdForLevel(skillsXPAchievePosition)){
            unlockAchievement(TOTAL_HERO_XP);
        }

        skillsXPAchievePosition = achievementsLevels.get(TOTAL_SKILLS_XP.ordinal());
        if (getStatisticsValue(TOTAL_SKILLS_XP_TAG) >= TOTAL_SKILLS_XP.getThresholdForLevel(skillsXPAchievePosition)){
            unlockAchievement(TOTAL_SKILLS_XP);
        }

        skillsXPAchievePosition = achievementsLevels.get(PERFORMED_TASKS.ordinal());
        if (getStatisticsValue(PERFORMED_TASKS_TAG) >= PERFORMED_TASKS.getThresholdForLevel(skillsXPAchievePosition)){
            unlockAchievement(PERFORMED_TASKS);
        }

        skillsXPAchievePosition = achievementsLevels.get(FINISHED_TASKS.ordinal());
        if (getStatisticsValue(FINISHED_TASKS_NUMBER_TAG) >= FINISHED_TASKS.getThresholdForLevel(skillsXPAchievePosition)){
            unlockAchievement(FINISHED_TASKS);
        }

        skillsXPAchievePosition = achievementsLevels.get(ADDED_TASKS.ordinal());
        if (getStatisticsValue(TOTAL_TASKS_NUMBER_TAG) >= ADDED_TASKS.getThresholdForLevel(skillsXPAchievePosition)){
            unlockAchievement(ADDED_TASKS);
        }

        skillsXPAchievePosition = achievementsLevels.get(TOP_LEVEL_SKILL.ordinal());
        if (!getAllSkills().isEmpty()
                && getAllSkills().get(0).getLevel() >= TOP_LEVEL_SKILL.getThresholdForLevel(skillsXPAchievePosition)){
            unlockAchievement(TOP_LEVEL_SKILL);
        }

        skillsXPAchievePosition = achievementsLevels.get(TOP_LEVEL_CHARACTERISTIC.ordinal());
        if (lifeEntity.getCharacteristics().get(0).getLevel() >= TOP_LEVEL_CHARACTERISTIC.getThresholdForLevel(skillsXPAchievePosition)){
            unlockAchievement(TOP_LEVEL_CHARACTERISTIC);
        }

        skillsXPAchievePosition = achievementsLevels.get(HERO_LEVEL.ordinal());
        if (getHero().getLevel() >= HERO_LEVEL.getThresholdForLevel(skillsXPAchievePosition)){
            unlockAchievement(HERO_LEVEL);
        }

        skillsXPAchievePosition = achievementsLevels.get(NUMBER_OF_SKILLS_WITH_LEVEL_10.ordinal());
        int threshold = (int) NUMBER_OF_SKILLS_WITH_LEVEL_10.getThresholdForLevel(skillsXPAchievePosition);
        if (getAllSkills().size() >= threshold &&
                getAllSkills().get(threshold - 1).getLevel() >= 10){
            unlockAchievement(NUMBER_OF_SKILLS_WITH_LEVEL_10);
        }

        skillsXPAchievePosition = achievementsLevels.get(NUMBER_OF_SKILLS_WITH_LEVEL_25.ordinal());
        threshold = (int) NUMBER_OF_SKILLS_WITH_LEVEL_25.getThresholdForLevel(skillsXPAchievePosition);
        if (getAllSkills().size() >= threshold &&
                getAllSkills().get(threshold - 1).getLevel() >= 25){
            unlockAchievement(NUMBER_OF_SKILLS_WITH_LEVEL_25);
        }

        skillsXPAchievePosition = achievementsLevels.get(NUMBER_OF_SKILLS_WITH_LEVEL_50.ordinal());
        threshold = (int) NUMBER_OF_SKILLS_WITH_LEVEL_50.getThresholdForLevel(skillsXPAchievePosition);
        if (getAllSkills().size() >= threshold &&
                getAllSkills().get(threshold - 1).getLevel() >= 50){
            unlockAchievement(NUMBER_OF_SKILLS_WITH_LEVEL_50);
        }

        skillsXPAchievePosition = achievementsLevels.get(NUMBER_OF_SKILLS_WITH_LEVEL_100.ordinal());
        threshold = (int) NUMBER_OF_SKILLS_WITH_LEVEL_100.getThresholdForLevel(skillsXPAchievePosition);
        if (getAllSkills().size() >= threshold &&
                getAllSkills().get(threshold - 1).getLevel() >= 100){
            unlockAchievement(NUMBER_OF_SKILLS_WITH_LEVEL_100);
        }
    }

    public boolean isFirstRun(){
        return getSharedPreferences().getBoolean(FIRTS_RUN_TAG, true);
    }

    public void setFirstRun(boolean isFirst){
        getSharedPreferences().edit().putBoolean(FIRTS_RUN_TAG, isFirst).apply();
    }

    public void updateMiscToDB(){
        updateAchievementsToMisc();
        updateStatisticsToMisc();
        lifeEntity.updateMiscToDB();
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
    }

    public Map<LocalDate, Integer> getTasksPerDayMap() {
        return lifeEntity.getTasksPerDay();
    }

    public void increaseTasksPerDay(int diff){
        Map<LocalDate, Integer> map = getTasksPerDayMap();
        LocalDate localDate = new LocalDate();
        Integer value = map.get(localDate);
        if (value == null) {
            lifeEntity.updateTasksPerDay(localDate, diff);
        } else {
            lifeEntity.updateTasksPerDay(localDate, value + diff);
        }

        checkTasksPerDay();
    }

    public void checkTasksPerDay() {
        LocalDate checkDay = new LocalDate();
        Map<LocalDate, Integer> map = getTasksPerDayMap();

        if (map.containsKey(checkDay)) return;
        lifeEntity.updateTasksPerDay(checkDay, 0);
        if (map.size() == 1) return;
        while (!map.containsKey(checkDay = checkDay.minusDays(1))) {
            lifeEntity.updateTasksPerDay(checkDay, 0);
        }
    }

    public void closeDBConnection(){
        lifeEntity.closeDBConnection();
    }

    public void removeAllAppProgress() {
        Hero hero = getHero();
        hero.reset();
        lifeEntity.updateHero(hero);

        List<Task> tasksCloned = new ArrayList<>();
        tasksCloned.addAll(getAllTasks());
        for (Task t : tasksCloned) {
            t.reset();
            updateTask(t);
        }

        List<Skill> skillsCloned = new ArrayList<>();
        skillsCloned.addAll(getAllSkills());
        for (Skill sk : skillsCloned) {
            sk.reset();
            updateSkill(sk);
        }

        List<Characteristic> charsCloned = new ArrayList<>();
        charsCloned.addAll(getCharacteristics());
        for (Characteristic ch: charsCloned) {
            ch.reset();
            updateCharacteristic(ch);
        }

        Misc.ACHIEVEMENTS_LEVELS = null;
        Misc.STATISTICS_NUMBERS = null;
        initStatistics();
        achievementsLevels.clear();
        for (int i = 0; i < AchievsList.values().length; i++) {
            achievementsLevels.add(0);
        }
        lifeEntity.updateMiscToDB();

        lifeEntity.removeTasksPerDayData();
    }

    public void openDBConnection(){
        lifeEntity.openDBConnection();
    }

    public void onDBFileUpdated(boolean isFileDeleted) {
        lifeEntity.onDBFileUpdated(isFileDeleted);
        initAchievements();
        initStatistics();

    }

    public boolean isDropBoxAutoBackupEnabled(){
        return getSharedPreferences().getBoolean(DROPBOX_AUTO_BACKUP_ENABLED, false);
    }

    private void performBackUpToDropBox(){
        if (isDropBoxAutoBackupEnabled()){
            final View view = currentActivity.getCurrentFragment().getView();
            if (view != null && System.currentTimeMillis() - dropboxBackupStartTime > dropboxBackupTimeout) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentActivity.checkAndBackupToDropBox(true);
                    }
                }, dropboxBackupTimeout);
                dropboxBackupStartTime = System.currentTimeMillis();
            }
        }
    }

    public int getScreenWidth() {
        Display display = currentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public void showDonationFragment() {
        currentActivity.switchToRootFragment(MainActivity.SETTINGS_FRAGMENT_ID);
        currentActivity.showChildFragment(new DonationFragment(), null);
    }
}
