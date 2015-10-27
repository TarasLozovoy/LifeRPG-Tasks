package com.levor.liferpg.Model;

public class Skill {
    private String title;
    private int level;
    private int sublevel;
    private Characteristic keyCharacteristic;

    public Skill(String title, Characteristic keyCharacteristic) {
        this(title, 1, 0, keyCharacteristic);
    }

    public Skill(String title, int level, int sublevel, Characteristic keyCharacteristic) {
        this.title = title;
        this.level = level;
        this.sublevel = sublevel;
        this.keyCharacteristic = keyCharacteristic;
    }

    public String getTitle() {
        return title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSublevel() {
        return sublevel;
    }

    public void setSublevel(int sublevel) {
        this.sublevel = sublevel;
    }

    public Characteristic getKeyCharacteristic() {
        return keyCharacteristic;
    }

    public void setKeyCharacteristic(Characteristic keyCharacteristic) {
        this.keyCharacteristic = keyCharacteristic;
    }

    public void increaseSublevel(){
        sublevel++;
        if (sublevel == level){
            level++;
            keyCharacteristic.increaseLevelByN(1 + level/10);
            sublevel = 0;
        }
    }

    public void decreaseSublevel(){
        sublevel--;
        if (sublevel < 0) {
            keyCharacteristic.increaseLevelByN(-(1 + level/10));
            level --;
            sublevel = level - 1;
        }
    }
}
