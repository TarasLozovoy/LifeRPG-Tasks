package com.levor.liferpg.Controller;

import com.levor.liferpg.Model.Characteristic;
import com.levor.liferpg.Model.LifeEntity;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class LifeController {
    private LifeEntity lifeEntity = LifeEntity.getInstance();

    private static LifeController LifeController;
    public static LifeController getInstance(){
        if (LifeController == null){
            LifeController = new LifeController();
        }
        return LifeController;
    }

    private LifeController() {}

    //======================================
    //Getters
    //======================================

    public Map<String, List<String>> getTasksWithRelatedSkills(){
        Map<String, List<String>> map = new TreeMap<>(); //Task title, related skills titles
        for (Task t : lifeEntity.getTasks().values()){
            List<String> skillsTitles = new ArrayList<>();
            for(Skill s : t.getRelatedSkills()){
                skillsTitles.add(s.getTitle());
            }
            map.put(t.getTitle(), skillsTitles);
        }

        return map;
    }

    public List<String> getTasksTitlesAsList(){
        List<String> titles = new ArrayList<>();
        for (Task t : lifeEntity.getTasks().values()){
            titles.add(t.getTitle());
        }
        return titles;
    }

    public Map<String, Integer[]> getSkillsTitlesAndLevels(){
        return lifeEntity.getSkillsTitlesAndLevels();
    }

    public ArrayList<Skill> getAllSkills(){
        return lifeEntity.getAllSkills();

//        Map<String, Integer[]> skills = lifeEntity.getAllSkills();
//        ArrayList<String> skillList = new ArrayList<>();
//        for (Map.Entry<String, Integer[]> pair : skills.entrySet()){
//            StringBuilder sb = new StringBuilder();
//            sb.append(pair.getKey())
//                    .append(" - ")
//                    .append(pair.getValue()[0])
//                    .append("(")
//                    .append(pair.getValue()[1])
//                    .append(")");
//            skillList.add(sb.toString());
//        }
//        return skillList.toArray(new String[skillList.size()]);
    }

    public String getCharacteristicRelatedToSkill(UUID id){
        return lifeEntity.getCharacteristicTitleBySkill(id);
    }

    public int getIntelligenceLevel(){
        return lifeEntity.getIntelligenceLevel();
    }

    public int getWisdomLevel(){
        return lifeEntity.getWisdomLevel();
    }
    public int getStrengthLevel(){
        return lifeEntity.getStrengthLevel();
    }
    public int getStaminaLevel(){
        return lifeEntity.getStaminaLevel();
    }
    public int getDexterityLevel(){
        return lifeEntity.getDexterityLevel();
    }

    public String getCurrentCharacteristicsString(){
        return lifeEntity.getCurrentCharacteristicsString();
    }

    public String getCurrentSkillsString(){
        return lifeEntity.getCurrentSkillsString();
    }

    public String getCurrentTasksString(){
        return  lifeEntity.getCurrentTasksString();
    }

    public Task getTaskByTitle(String s) {
        return lifeEntity.getTaskByTitle(s);
    }

    //======================================
    //Setters
    //======================================

    public void updateCurrentContentWithStrings(String characteristicsFromFile, String skillsFromFile, String tasksFromFile) {
        //characteristics
        String[] characteristics = characteristicsFromFile.split(":;");
        for (String characteristic : characteristics){
            if (!characteristic.equals("")) {
                String[] subelements = characteristic.split("::");
                try {
                    lifeEntity.updateCharacteristic(subelements[0], Integer.parseInt(subelements[1]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //skills
        String[] skills = skillsFromFile.split(":;");
        for (String skill : skills){
            if (!skill.equals("")) {
                String[] subelements = skill.split("::");
                try {
                    lifeEntity.updateSkill(subelements[0], Integer.parseInt(subelements[1]),
                            Integer.parseInt(subelements[2]),UUID.fromString(subelements[3]), subelements[4]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //tasks
        String[] tasks = tasksFromFile.split(":;");
        for (String task : tasks){
            if (!task.equals("")) {
                String[] subelements = task.split("::");
                String[] relatedSkillsTitles = new String[subelements.length - 2];
                for (int i = 0; i < relatedSkillsTitles.length; i++){
                    relatedSkillsTitles[i] = subelements[i + 2];
                }
                try {
                    lifeEntity.updateTask(subelements[0], UUID.fromString(subelements[1]), relatedSkillsTitles);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void createNewTask(String title, List<String> relatedSkills) {
        Skill[] skills = new Skill[relatedSkills.size()];
        for (int i = 0; i < relatedSkills.size(); i++){
            skills[i] = lifeEntity.getSkillByTitle(relatedSkills.get(i));
        }
        lifeEntity.addTask(title, skills);
    }

    public void createNewSkill(String title, Characteristic keyChar){
        lifeEntity.addSkill(title, keyChar);
    }

    public Skill getSkillByTitle(String title) {
        return lifeEntity.getSkillByTitle(title);
    }

    public ArrayList<Task> getTasksBySkill(Skill sk){
        return lifeEntity.getTasksBySkill(sk);
    }

    public void removeTask(Task task) {
        lifeEntity.removeTask(task);
    }

    public String[] getCharacteristicsTitleAndLevelAsArray(){
        List<Characteristic> characteristics = lifeEntity.getAllCharacteristics();
        ArrayList<String> strings = new ArrayList<>();
        for (Characteristic ch : characteristics){
            strings.add(ch.getTitle() + " - " + ch.getLevel());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public String[] getCharacteristicsTitlesArray(){
        List<Characteristic> characteristics = lifeEntity.getAllCharacteristics();
        ArrayList<String> strings = new ArrayList<>();
        for (Characteristic ch : characteristics){
            strings.add(ch.getTitle());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public Characteristic getCharacteristicByTitle(String title) {
        try {
            return lifeEntity.getCharacteristicByTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
}
