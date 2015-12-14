package com.levor.liferpg.Controller;

import android.content.Context;

import com.levor.liferpg.Model.Characteristic;
import com.levor.liferpg.Model.Hero;
import com.levor.liferpg.Model.LifeEntity;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LifeController {
    private LifeEntity lifeEntity;

    private static LifeController LifeController;
    public static LifeController getInstance(Context context){
        if (LifeController == null){
            LifeController = new LifeController(context);
        }
        return LifeController;
    }

    private LifeController(Context context) {
        lifeEntity = LifeEntity.getInstance(context);
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

    public void createNewTask(String title, int repeatability, int difficulty, int reproducibility, Date date, List<String> relatedSkills) {
        lifeEntity.addTask(title, repeatability, difficulty, reproducibility, date,  relatedSkills);
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

    public boolean performTask(Task task){
        Hero hero = lifeEntity.getHero();
        if (task.getRepeatability() > 0){
            task.setRepeatability(task.getRepeatability() - 1);
            updateTask(task);
        }
        double multiplier = task.getMultiplier();
        double finalXP = hero.getBaseXP() * multiplier;
        for (Skill sk : task.getRelatedSkills()) {
            if (sk.increaseSublevel(finalXP)){
                lifeEntity.updateCharacteristic(sk.getKeyCharacteristic());
            }
            updateSkill(sk);
        }
        boolean isLevelIncreased = hero.increaseXP(finalXP);
        lifeEntity.updateHero(hero);

        return isLevelIncreased;
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
}
