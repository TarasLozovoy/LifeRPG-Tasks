package com.levor.liferpg.Controller;

import com.levor.liferpg.Model.LifeEntity;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;

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
}
