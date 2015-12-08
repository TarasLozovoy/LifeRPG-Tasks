package com.levor.liferpg.Model;

import java.util.UUID;

public class Skill {
    private String title;
    private int level;
    private int sublevel;
    private Characteristic keyCharacteristic;
    private UUID id;

    public Skill(String title, UUID id, Characteristic keyCharacteristic) {
        this(title, 1, 0, id, keyCharacteristic);
    }

    public Skill(String title, int level, int sublevel, UUID id, Characteristic keyCharacteristic) {
        this.title = title;
        this.level = level;
        this.sublevel = sublevel;
        this.keyCharacteristic = keyCharacteristic;
        this.id = id;
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

    /**
     * @return true if level changed
     */
    public boolean increaseSublevel(){
        sublevel++;
        if (sublevel == level){
            level++;
            keyCharacteristic.increaseLevelByN(1 + level/10);
            sublevel = 0;
            return true;
        }
        return false;
    }

    /**
     * @return true if level changed
     */
    public boolean decreaseSublevel(){
        sublevel--;
        if (sublevel < 0) {
            keyCharacteristic.increaseLevelByN(-(1 + level/10));
            level --;
            sublevel = level - 1;
            return true;
        }
        return false;
    }

    public UUID getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Skill){
            Skill that = (Skill) o;
            return id.equals(that.id);
        } else {
            return false;
        }
    }
}
