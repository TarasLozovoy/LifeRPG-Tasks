package com.levor.liferpg.Model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LifeEntity {
    private final Characteristic intelligence = new Characteristic("Intelligence", 1);
    private final Characteristic wisdom = new Characteristic("Wisdom", 1);
    private final Characteristic strength = new Characteristic("Strength", 1);
    private final Characteristic stamina = new Characteristic("Stamina", 1);
    private final Characteristic dexterity = new Characteristic("Dexterity", 1);

    private Map<String, Skill> skills = new TreeMap<>();
    private Map<String, Task> tasks = new TreeMap<>();

    private static LifeEntity lifeEntity;

    public static LifeEntity getInstance(){
        if (lifeEntity == null){
            lifeEntity = new LifeEntity();
        }
        return lifeEntity;
    }

    private LifeEntity() {
        skills.put("Android", new Skill("Android", intelligence));
        skills.put("Java", new Skill("Java", intelligence));
        skills.put("Erudition", new Skill("Erudition", wisdom));
        skills.put("English", new Skill("English", intelligence));
        skills.put("Powerlifting", new Skill("Powerlifting", strength));
        skills.put("Roller skating", new Skill("Roller skating", stamina));
        skills.put("Running", new Skill("Running", stamina));

        addTask("Learn Android", skills.get("Android"));
        addTask("Learn Java", skills.get("Java"));
        addTask("Fix easy bug on Android", skills.get("Android"));
        addTask("Fix medium bug on Android", skills.get("Android"));
        addTask("Fix difficult bug on Android", skills.get("Android"));
        addTask("Fix easy bug on Java", skills.get("Java"));
        addTask("Fix medium bug on Java", skills.get("Java"));
        addTask("Fix difficult bug on Java", skills.get("Java"));

        addTask("Read fiction book", skills.get("Erudition"));
        addTask("Read self-development book", skills.get("Erudition"));
        addTask("Read Android book", skills.get("Erudition"), skills.get("Android"));
        addTask("Read Java book", skills.get("Erudition"), skills.get("Java"));

        addTask("Read fiction book on English", skills.get("Erudition"), skills.get("English"));
        addTask("Read self-development book on English", skills.get("Erudition"), skills.get("English"));
        addTask("Read Android book on English", skills.get("Erudition"), skills.get("Android"), skills.get("English"));
        addTask("Read Java book on English", skills.get("Erudition"), skills.get("Java"), skills.get("English"));
        addTask("Learn English", skills.get("English"));

        addTask("Run 5 km", skills.get("Running"));
        addTask("Run 10 km", skills.get("Running"));
        addTask("Run 15 km", skills.get("Running"));

        addTask("Ride for 10 km", skills.get("Roller skating"));
        addTask("Ride for 20 km", skills.get("Roller skating"));
        addTask("Ride for 30 km", skills.get("Roller skating"));
        addTask("Ride for 40 km", skills.get("Roller skating"));

        addTask("Train in gym", skills.get("Powerlifting"));
    }

    public Map<String, Integer[]> getSkillsTitlesAndLevels() {
        Map<String, Integer[]> map = new TreeMap<>();
        for (Skill s : skills.values()) {
            map.put(s.getTitle(), new Integer[]{s.getLevel(), s.getSublevel()});
        }
        return map;
    }

    public Map<String, Task> getTasks() {
        return tasks;
    }

    public void addTask(String title, Skill ... relatedSkills){
        tasks.put(title, new Task(title, relatedSkills));
    }

    private List<Characteristic> getAllCharacteristics(){
        ArrayList<Characteristic> list = new ArrayList<>();
        list.add(intelligence);
        list.add(wisdom);
        list.add(strength);
        list.add(stamina);
        list.add(dexterity);
        return list;
    }

    public int getIntelligenceLevel(){
        return intelligence.getLevel();
    }

    public int getWisdomLevel(){
        return wisdom.getLevel();
    }

    public int getStrengthLevel(){
        return strength.getLevel();
    }

    public int getStaminaLevel(){
        return stamina.getLevel();
    }

    public int getDexterityLevel(){
        return dexterity.getLevel();
    }

    public String getCharacteristicTitleBySkill(String skillTitle){
        return skills.get(skillTitle).getKeyCharacteristic().getTitle();
    }

    public String getCurrentCharacteristicsString() {
        StringBuilder sb = new StringBuilder();
        for (Characteristic ch : getAllCharacteristics()){
            sb.append(ch.getTitle())
                    .append("::")
                    .append(ch.getLevel())
                    .append(":;");
        }
        return sb.toString();
    }

    public String getCurrentSkillsString() {
        StringBuilder sb = new StringBuilder();
        for (Skill sk : skills.values()){
            sb.append(sk.getTitle())
                    .append("::")
                    .append(sk.getLevel())
                    .append("::")
                    .append(sk.getSublevel())
                    .append("::")
                    .append(sk.getKeyCharacteristic().getTitle())
                    .append(":;");
        }
        return sb.toString();
    }

    public String getCurrentTasksString() {
        StringBuilder sb = new StringBuilder();
        for(Task t : tasks.values()){
            sb.append(t.getTitle())
                    .append("::");
            for (int i = 0; i < t.getRelatedSkills().size(); i++){
                sb.append(t.getRelatedSkills().get(i).getTitle())
                        .append(i == t.getRelatedSkills().size() - 1 ? ":;" : "::");
            }
        }
        return sb.toString();
    }
}
