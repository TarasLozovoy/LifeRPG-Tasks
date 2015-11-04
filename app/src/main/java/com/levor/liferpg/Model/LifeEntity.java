package com.levor.liferpg.Model;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class LifeEntity {
    private final Characteristic intelligence = new Characteristic("Intelligence", 1);
    private final Characteristic wisdom = new Characteristic("Wisdom", 1);
    private final Characteristic strength = new Characteristic("Strength", 1);
    private final Characteristic stamina = new Characteristic("Stamina", 1);
    private final Characteristic dexterity = new Characteristic("Dexterity", 1);

    private Map<UUID, Skill> skills = new TreeMap<>();  //title, skill
    private Map<UUID, Task> tasks = new TreeMap<>();    //title, task

    private static LifeEntity lifeEntity;

    public static LifeEntity getInstance(){
        if (lifeEntity == null){
            lifeEntity = new LifeEntity();
        }
        return lifeEntity;
    }

    private LifeEntity() {
        addSkill("Android", intelligence);
        addSkill("Java", intelligence);
        addSkill("Erudition", wisdom);
        addSkill("English", intelligence);
        addSkill("Powerlifting", strength);
        addSkill("Roller skating", stamina);
        addSkill("Running", stamina);

        try {
            addTask("Learn Android", getSkillByTitle("Android"));
            addTask("Learn Java", getSkillByTitle("Java"));
            addTask("Fix bug on Android", getSkillByTitle("Android"));
            addTask("Fix bug on Java", getSkillByTitle("Java"));
//
//        addTask("Read fiction book", skills.get("Erudition"));
//        addTask("Read self-development book", skills.get("Erudition"));
//        addTask("Read Android book", skills.get("Erudition"), skills.get("Android"));
//        addTask("Read Java book", skills.get("Erudition"), skills.get("Java"));
//
//        addTask("Read fiction book on English", skills.get("Erudition"), skills.get("English"));
//        addTask("Read self-development book on English", skills.get("Erudition"), skills.get("English"));
//        addTask("Read Android book on English", skills.get("Erudition"), skills.get("Android"), skills.get("English"));
//        addTask("Read Java book on English", skills.get("Erudition"), skills.get("Java"), skills.get("English"));
//        addTask("Learn English", skills.get("English"));
//
//        addTask("Run 5 km", skills.get("Running"));
//        addTask("Run 10 km", skills.get("Running"));
//        addTask("Run 15 km", skills.get("Running"));
//
//        addTask("Ride for 10 km", skills.get("Roller skating"));
//        addTask("Ride for 20 km", skills.get("Roller skating"));
//        addTask("Ride for 30 km", skills.get("Roller skating"));
//        addTask("Ride for 40 km", skills.get("Roller skating"));
//
//        addTask("Train in gym", skills.get("Powerlifting"));
        } catch (IOException e){}
    }

    public Map<String, Integer[]> getSkillsTitlesAndLevels() {
        Map<String, Integer[]> map = new TreeMap<>();
        for (Skill s : skills.values()) {
            map.put(s.getTitle(), new Integer[]{s.getLevel(), s.getSublevel()});
        }
        return map;
    }

    public Map<UUID, Task> getTasks() {
        return tasks;
    }

    public void addTask(String title, Skill ... relatedSkills){
        UUID id = UUID.randomUUID();
        tasks.put(id, new Task(title, id, relatedSkills));
    }

    public void addSkill(String title, Characteristic keyCharacteristic){
        UUID id = UUID.randomUUID();
        addSkill(title, 1, 0, id, keyCharacteristic);
    }

    public void addSkill(String title, int level, int sublevel, UUID id, Characteristic keyCharacteristic){
        Skill sk = new Skill(title, level, sublevel, id, keyCharacteristic);
        skills.put(id, sk);
    }

    public List<Characteristic> getAllCharacteristics(){
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

    public String getCharacteristicTitleBySkill(UUID id){
        return skills.get(id).getKeyCharacteristic().getTitle();
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
                    .append(sk.getId())
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
                    .append("::")
                    .append(t.getId())
                    .append("::");
            for (int i = 0; i < t.getRelatedSkills().size(); i++){
                sb.append(t.getRelatedSkills().get(i).getTitle())
                        .append(i == t.getRelatedSkills().size() - 1 ? ":;" : "::");
            }
        }
        return sb.toString();
    }

    public void updateCharacteristic(String title, int level) throws IOException {
        getCharacteristicByTitle(title).setLevel(level);
    }

    public void updateSkill(String title, int level, int sublevel,UUID id, String keyCharacteristicTitle) throws IOException {
        if (skills.containsKey(id)){
            Skill updSkill = skills.get(id);
            updSkill.setLevel(level);
            updSkill.setSublevel(sublevel);
            updSkill.setKeyCharacteristic(getCharacteristicByTitle(keyCharacteristicTitle));
        } else {
            addSkill(title, level, sublevel, id, getCharacteristicByTitle(keyCharacteristicTitle));
        }
    }

    public void updateTask(String title,UUID id, String ... relatedSkillsTitles) throws IOException {
        if (tasks.containsKey(id)){
            Task updTask = tasks.get(id);
            Skill[] skills = new Skill[relatedSkillsTitles.length];
            for (int i = 0; i < relatedSkillsTitles.length; i++){
                skills[i] = getSkillByTitle(relatedSkillsTitles[i]);
            }
            updTask.addRelatedSkills(skills);

        } else {
            Skill[] relatedSkills = new Skill[relatedSkillsTitles.length];
            for (int i = 0; i < relatedSkillsTitles.length; i++){
                relatedSkills[i] = getSkillByTitle(relatedSkillsTitles[i]);
            }
            addTask(title, relatedSkills);
        }
    }

    public Characteristic getCharacteristicByTitle(String title) throws IOException {
        for (Characteristic ch : getAllCharacteristics()){
            if (ch.getTitle().equals(title)){
                return ch;
            }
        }
        throw new IOException("Skill with current title not found");
    }

    public Skill getSkillByTitle(String title) throws IOException {
        for (Skill sk : skills.values()){
            if (sk.getTitle().equals(title)){
                return sk;
            }
        }
        throw new IOException("Skill with current title not found");
    }

    public Skill getSkillByID(UUID id){
        return skills.get(id);
    }

    public ArrayList<Skill> getAllSkills(){
        return new ArrayList<>(skills.values());
    }

    public ArrayList<Task> getTasksBySkill(Skill sk){
        ArrayList<Task> tasksBySkill = new ArrayList<>();
        for (Task t : tasks.values()){
            if (t.getRelatedSkills().contains(sk)){
                tasksBySkill.add(t);
            }
        }
        return tasksBySkill;
    }

    public ArrayList<Skill> getSkillsByCharacteristic(Characteristic ch){
        ArrayList<Skill> sk = new ArrayList<>();
        for (Skill skill : skills.values()){
            if (skill.getKeyCharacteristic().equals(ch)){
                sk.add(skill);
            }
        }
        return sk;
    }


    public void removeTask(Task task) {
        tasks.remove(task.getId());
    }

    public Task getTaskByID(UUID id) {
        return tasks.get(id);
    }

    public Task getTaskByTitle(String s) {
        for (Task t: tasks.values()){
            if (t.getTitle().equals(s)){
                return t;
            }
        }
        return null;
    }
}
