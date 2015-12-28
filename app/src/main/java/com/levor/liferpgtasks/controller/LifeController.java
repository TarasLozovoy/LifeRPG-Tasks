package com.levor.liferpgtasks.controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.widget.Toast;

import com.levor.liferpgtasks.broadcastReceivers.TaskNotification;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Hero;
import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LifeController {
    public static final String TASK_TITLE_NOTIFICATION_TAG = "task_id_notification_ tag";
    private LifeEntity lifeEntity;
    private Context context;

    private boolean activityPaused;

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
    }

    public boolean isActivityPaused() {
        return activityPaused;
    }

    public void setActivityPaused(boolean activityPaused) {
        this.activityPaused = activityPaused;
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
    }


    public void updateTask(Task task) {
        lifeEntity.updateTask(task);
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

    public boolean performTask(Task task, boolean changeRepeatability){
        Hero hero = lifeEntity.getHero();
        task.setUndonable(true);
        if (changeRepeatability && task.getRepeatability() > 0){
            task.setRepeatability(task.getRepeatability() - 1);
        }
        updateTask(task);
        double multiplier = task.getMultiplier();
        double finalXP = hero.getBaseXP() * multiplier;
        for (Skill sk : task.getRelatedSkills()) {
            if (changeRepeatability && sk.increaseSublevel(finalXP)){
                lifeEntity.updateCharacteristic(sk.getKeyCharacteristic());
            }
            updateSkill(sk);
        }
        boolean isLevelIncreased = hero.increaseXP(finalXP);
        lifeEntity.updateHero(hero);

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
        }
        boolean isLevelChanged = hero.decreaseXP(finalXP);
        lifeEntity.updateHero(hero);

        return isLevelChanged;
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
}
