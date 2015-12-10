package com.levor.liferpg.Model;

import java.util.Comparator;
import java.util.UUID;

public class Skill {
    private String title;
    private int level;
    private int sublevel;
    private Characteristic keyCharacteristic;
    private UUID id;

    public static final Comparator<Skill> TITLE_COMPARATOR = new SkillByTitleComparator();
    public static final Comparator<Skill> LEVEL_COMPARATOR = new SkillByLevelComparator();

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
        if (!(o instanceof Skill)) return false;
        else return this.id.equals(((Skill) o).id);
    }

    private static class SkillByTitleComparator implements Comparator<Skill>{

        @Override
        public int compare(Skill lhs, Skill rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }

    private static class SkillByLevelComparator implements Comparator<Skill>{

        @Override
        public int compare(Skill lhs, Skill rhs) {
            if (rhs.getLevel() != lhs.getLevel()) {
                return rhs.getLevel() - lhs.getLevel();
            } else if (rhs.getSublevel() != lhs.getSublevel()) {
                return rhs.getSublevel() - lhs.getSublevel();
            } else {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        }
    }
}
