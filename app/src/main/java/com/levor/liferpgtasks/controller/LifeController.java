package com.levor.liferpgtasks.controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.levor.liferpgtasks.AchievsList;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.broadcastReceivers.TaskNotification;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Hero;
import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.widget.LifeRPGWidgetProvider;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static com.levor.liferpgtasks.AchievsList.*;

public class LifeController {
    public static final String TASK_TITLE_NOTIFICATION_TAG = "task_id_notification_ tag";
    public static final String SHARED_PREFS_TAG = "shared_prefs_tag";
    public static final String PERFORMED_TASKS_TAG= "performed_task_tag";
    public static final String TOTAL_TASKS_NUMBER_TAG= "total_tasks_number_tag";
    public static final String TOTAL_HERO_XP_TAG= "total_hero_xp_tag";
    public static final String TOTAL_SKILLS_XP_TAG= "total_skills_xp_tag";
    public static final String XP_MULTIPLIER_TAG= "xp_multiplier_tag";
    public static final String ACHIEVEMENTS_TAG= "achievements_tag";
    private LifeEntity lifeEntity;
    private Context context;
    private Tracker tracker;
    private List<Integer> achievmentsLevels = new ArrayList<>();

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
    }

    public void setGATracker(Tracker tracker){
        this.tracker = tracker;
    }

    public Tracker getGATracker() {
        return tracker;
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

    public void createNewTask(String title, int repeatability, int difficulty, int reproducibility,
                              Date date, boolean notify, List<String> relatedSkills) {
        lifeEntity.addTask(title, repeatability, difficulty, reproducibility, date, notify, relatedSkills);
        updateHomeScreenWidgets();
        updateStatistics(TOTAL_TASKS_NUMBER_TAG, 1);
    }

    public void updateTask(Task task) {
        lifeEntity.updateTask(task);
        updateHomeScreenWidgets();
    }

    public void addSkill(String title, Characteristic keyChar){
        lifeEntity.addSkill(title, keyChar);
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
    }

    public String[] getCharacteristicsTitleAndLevelAsArray(){
        List<Characteristic> characteristics = lifeEntity.getCharacteristics();
        ArrayList<String> strings = new ArrayList<>();
        for (Characteristic ch : characteristics){
            strings.add(ch.getTitle() + " - " + ch.getLevel());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public String[] getCharacteristicsTitlesArray(){
        List<Characteristic> characteristics = lifeEntity.getCharacteristics();
        ArrayList<String> strings = new ArrayList<>();
        for (Characteristic ch : characteristics){
            strings.add(ch.getTitle());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public Characteristic getCharacteristicByTitle(String title) {
        return lifeEntity.getCharacteristicByTitle(title);
    }

    public ArrayList<Skill> getSkillsByCharacteristic(Characteristic ch) {
        return lifeEntity.getSkillsByCharacteristic(ch);
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
            List<Skill> newSkills = new ArrayList<>();
            for (Skill sk : t.getRelatedSkills()){
                if (!sk.equals(skill)){
                    newSkills.add(sk);
                }
            }
            t.setRelatedSkills(newSkills);
        }
        lifeEntity.removeSkill(skill);
    }

    public boolean performTask(Task task){
        Hero hero = lifeEntity.getHero();
        task.setUndonable(true);
        if (task.getRepeatability() > 0){
            task.setRepeatability(task.getRepeatability() - 1);
        }
        updateTask(task);
        double multiplier = task.getMultiplier();
        double finalXP = hero.getBaseXP() * multiplier;
        for (Skill sk : task.getRelatedSkills()) {
            if (sk.increaseSublevel(finalXP)){
                lifeEntity.updateCharacteristic(sk.getKeyCharacteristic());
            }
            updateSkill(sk);
            updateStatistics(TOTAL_SKILLS_XP_TAG, (float) finalXP);
        }
        boolean isLevelIncreased = hero.increaseXP(finalXP);
        lifeEntity.updateHero(hero);
        updateStatistics(TOTAL_HERO_XP_TAG, (float) finalXP);

        //check for hero xp achievement
        if (hero.getTotalXP() >= TOTAL_HERO_XP.getThresholdForLevel(achievmentsLevels.get(TOTAL_HERO_XP.ordinal()))){
            unlockAchievement(TOTAL_HERO_XP);
            hero.setBaseXP(hero.getBaseXP() + (TOTAL_HERO_XP.getReward() * 0.01));
        }

        if (isLevelIncreased){
            getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(context.getString(R.string.GA_action))
                    .setAction(context.getString(R.string.GA_hero_level_increased) + " " + hero.getLevel())
                    .build());
        }

        updateStatistics(PERFORMED_TASKS_TAG, 1);
        return isLevelIncreased;
    }

    public boolean undoTask(Task task){
        Hero hero = lifeEntity.getHero();
        task.setUndonable(false);
        if (task.getRepeatability() >= 0){
            task.setRepeatability(task.getRepeatability() + 1);
            updateTask(task);
        }
        double multiplier = task.getMultiplier();
        double finalXP = hero.getBaseXP() * multiplier;
        for (Skill sk : task.getRelatedSkills()) {
            if (sk.decreaseSublevel(finalXP)){
                lifeEntity.updateCharacteristic(sk.getKeyCharacteristic());
            }
            updateSkill(sk);
            updateStatistics(TOTAL_SKILLS_XP_TAG, (float) -finalXP);
        }
        boolean isLevelChanged = hero.decreaseXP(finalXP);
        lifeEntity.updateHero(hero);
        updateStatistics(TOTAL_HERO_XP_TAG, (float) -finalXP);

        updateStatistics(PERFORMED_TASKS_TAG, -1);
        return isLevelChanged;
    }

    public boolean shareTask(Task task){
        Hero hero = lifeEntity.getHero();
        double multiplier = task.getShareMultiplier();
        double finalXP = hero.getBaseXP() * multiplier;
        boolean isLevelIncreased = hero.increaseXP(finalXP);
        lifeEntity.updateHero(hero);
        updateStatistics(TOTAL_HERO_XP_TAG, (float) finalXP);

        if (isLevelIncreased){
            getGATracker().send(new HitBuilders.EventBuilder()
                    .setCategory(context.getString(R.string.GA_action))
                    .setAction(context.getString(R.string.GA_hero_level_increased) + " " + hero.getLevel())
                    .build());
        }
        return  isLevelIncreased;
    }

    public void updateSkill(Skill skill) {
        lifeEntity.updateSkill(skill);
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
    }

    public void setupTasksNotifications(){
        for (Task t: getAllTasks()){
            updateTaskNotification(t);
        }
    }

    public void updateTaskNotification(Task task){
        removeTaskNotification(task);
        addTaskNotification(task);
    }

    public void addTaskNotification(Task task){
        Date currentDate = new Date(System.currentTimeMillis());
        if (task.getDate().before(currentDate)) return;
        Intent intent = new Intent(context, TaskNotification.class);
        intent.putExtra(TASK_TITLE_NOTIFICATION_TAG, task.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                task.getId().hashCode(), intent, 0);
        Calendar cal = Calendar.getInstance();
        cal.setTime(task.getDate());
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        long repeatTime = 24 * 60 * 60 * 1000;
        if (task.getRepeatability() > 0) {
            repeatTime = 5 * 24 * 60 * 60 * 1000;
        }
        if (task.getRepeatability() != 0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), repeatTime, pendingIntent);
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

    private void updateStatistics(String field, float value){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        switch (field) {
            case PERFORMED_TASKS_TAG :
                float tasksPerformed = prefs.getFloat(PERFORMED_TASKS_TAG, 0);
                tasksPerformed += value;
                prefs.edit().putFloat(PERFORMED_TASKS_TAG, tasksPerformed).apply();
                break;
            case TOTAL_TASKS_NUMBER_TAG :
                float totalTasks = prefs.getFloat(TOTAL_TASKS_NUMBER_TAG, 0);
                totalTasks += value;
                prefs.edit().putFloat(TOTAL_TASKS_NUMBER_TAG, totalTasks).apply();
                break;
            case TOTAL_HERO_XP_TAG :
                float totalHeroXP = prefs.getFloat(TOTAL_HERO_XP_TAG, 0);
                totalHeroXP += value;
                prefs.edit().putFloat(TOTAL_HERO_XP_TAG, totalHeroXP).apply();
                break;
            case TOTAL_SKILLS_XP_TAG :
                float totalSkillsXP = prefs.getFloat(TOTAL_SKILLS_XP_TAG, 0);
                totalSkillsXP += value;
                prefs.edit().putFloat(TOTAL_SKILLS_XP_TAG, totalSkillsXP).apply();
                break;
            case XP_MULTIPLIER_TAG :
                Hero hero = getHero();
                hero.setBaseXP(hero.getBaseXP() + value);
                lifeEntity.updateHero(hero);
        }
    }

    public float getStatisticsValue(String field){
        switch (field) {
            case XP_MULTIPLIER_TAG:
                return (float)getHero().getBaseXP();
            default:
                SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                return prefs.getFloat(field, 0);
        }
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

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        LinkedHashSet<String> defaultSet = (LinkedHashSet<String>) prefs.getStringSet(ACHIEVEMENTS_TAG, null);
        achievmentsLevels.clear();
        if (defaultSet == null) {
            for (int i = 0; i < AchievsList.values().length; i++) {
                achievmentsLevels.add(0);
            }
        } else {
            for (String s : defaultSet) {
                achievmentsLevels.add(Integer.parseInt(s));
            }
        }
    }

    private void updateAchievements(){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (Integer i : achievmentsLevels){
            set.add(String.valueOf(i));
        }
        prefs.edit().putStringSet(ACHIEVEMENTS_TAG, set).apply();
    }

    private void unlockAchievement(AchievsList achievement){
        int position = achievement.ordinal();
        achievmentsLevels.set(position, achievmentsLevels.get(position) + 1);
        updateAchievements();
        String achievementString = String.format(achievement.getDescription(),
                achievement.getThresholdForLevel(achievmentsLevels.get(position)));
        Toast.makeText(context,
                context.getString(R.string.achievement_unlocked, achievementString) + "\n" +
                        context.getString(R.string.xp_multiplier_reward, achievement.getReward()),
                Toast.LENGTH_LONG).show();
    }

    public List<Integer> getAchievementsLevels(){
        return  achievmentsLevels;
    }
}
