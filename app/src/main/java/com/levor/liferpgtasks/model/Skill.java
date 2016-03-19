package com.levor.liferpgtasks.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Skill {
    private String title;
    private int level;
    private double sublevel;
    private List<Characteristic> keyCharacteristicsList;
    private UUID id;

    public static final Comparator<Skill> TITLE_COMPARATOR = new SkillByTitleComparator();
    public static final Comparator<Skill> LEVEL_COMPARATOR = new SkillByLevelComparator();

    public Skill(String title, UUID id, List<Characteristic> keyCharacteristicsList) {
        this(title, 1, 0.0f, id, keyCharacteristicsList);
    }

    public Skill(String title, int level, double sublevel, UUID id, List<Characteristic> keyCharacteristicsList) {
        this.title = title;
        this.level = level;
        this.sublevel = sublevel;
        this.keyCharacteristicsList = keyCharacteristicsList;
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

    public double getSublevel() {
        return sublevel;
    }

    public void setSublevel(double sublevel) {
        this.sublevel = sublevel;
    }

    public List<Characteristic> getKeyCharacteristicsList() {
        return keyCharacteristicsList;
    }

    public void setKeyCharacteristicsList(List<Characteristic> keyCharacteristicsList) {
        this.keyCharacteristicsList = keyCharacteristicsList;
    }

    public void addKeyCharacteristic(Characteristic characteristic){
        if (characteristic != null && !keyCharacteristicsList.contains(characteristic)) {
            keyCharacteristicsList.add(characteristic);
        }
    }

    public void removeKeyCharacteristic(Characteristic characteristic){
        if (characteristic != null) {
            keyCharacteristicsList.remove(characteristic);
        }
    }

    public void removeAllKeyCharacteristics() {
        keyCharacteristicsList.clear();
    }

    public String getKeyCharacteristicsString() {
        Collections.sort(keyCharacteristicsList, Characteristic.LEVEL_COMPARATOR);
        StringBuilder sb = new StringBuilder();
        for (Characteristic ch : keyCharacteristicsList) {
            sb.append(ch.getTitle())
                    .append("::");
        }
        return sb.toString();
    }

    /**
     * @return true if level changed
     */
    public boolean increaseSublevel(double value){
        sublevel += value;
        if (sublevel >= ((double)level)){
            sublevel = sublevel - ((double) level);
            level++;
            for (Characteristic ch : keyCharacteristicsList) {
                ch.increaseLevelByN(1 + level / 10);
            }
            return true;
        }
        return false;
    }

    /**
     * @return true if level changed
     */
    public boolean decreaseSublevel(double value){
        sublevel -= value;
        if (sublevel < 0.0d) {
            for (Characteristic ch : keyCharacteristicsList) {
                ch.increaseLevelByN(-(1 + level / 10));
            }
            level --;
            sublevel = ((double) level) + sublevel;
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

    /**
     * Compares skills by level. Skill with highest level will be first.
     */
    private static class SkillByLevelComparator implements Comparator<Skill>{

        @Override
        public int compare(Skill lhs, Skill rhs) {
            if (rhs.getLevel() != lhs.getLevel()) {
                return rhs.getLevel() - lhs.getLevel();
            } else if (rhs.getSublevel() != lhs.getSublevel()) {
                return (int)(rhs.getSublevel() - lhs.getSublevel());
            } else {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        }
    }
}
