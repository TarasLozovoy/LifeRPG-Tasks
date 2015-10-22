package com.levor.liferpg.Controller;

import com.levor.liferpg.Model.LifeEntity;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LifeController {
    private LifeEntity lifeEntity = LifeEntity.getInstance();
    Map<String, Task> tasks;

    private static LifeController LifeController;
    public static LifeController getInstance(){
        if (LifeController == null){
            LifeController = new LifeController();
        }
        return LifeController;
    }

    private LifeController() {
        tasks = lifeEntity.getTasks();
    }

    //======================================
    //Getters
    //======================================

    public Map<String, List<String>> getTasksWithRelatedSkills(){
        Map<String, List<String>> map = new TreeMap<>(); //Task title, related skills titles
        for (Task t : tasks.values()){
            List<String> skillsTitles = new ArrayList<>();
            for(Skill s : t.getRelatedSkills()){
                skillsTitles.add(s.getTitle());
            }
            map.put(t.getTitle(), skillsTitles);
        }

        return map;
    }

    public Map<String, Integer[]> getSkillsTitlesAndLevels(){
        return lifeEntity.getSkillsTitlesAndLevels();
    }

    public String getCharacteristicRelatedToSkill(String skillTitle){
        return lifeEntity.getCharacteristicTitleBySkill(skillTitle);
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
                            Integer.parseInt(subelements[2]), subelements[3]);
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
                String[] relatedSkillsTitles = new String[subelements.length - 1];
                for (int i = 0; i < relatedSkillsTitles.length; i++){
                    relatedSkillsTitles[i] = subelements[i + 1];
                }
                try {
                    lifeEntity.updateTask(subelements[0], relatedSkillsTitles);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
