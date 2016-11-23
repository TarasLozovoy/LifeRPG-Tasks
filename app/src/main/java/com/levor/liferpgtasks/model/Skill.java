package com.levor.liferpgtasks.model;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Skill implements Comparable<Skill> {
    public static final String CHAR_CHAR_DB_DIVIDER = "::";
    public static final String CHAR_IMPACT_DB_DIVIDER = ":-:";

    private String title;
    private int level;
    private double sublevel;
    private TreeMap<Characteristic, Integer> keyCharsImpactMap;
    private UUID id;

    public static final Comparator<Skill> TITLE_COMPARATOR = new SkillByTitleComparator();
    public static final Comparator<Skill> LEVEL_COMPARATOR = new SkillByLevelComparator();

    public Skill(String title, UUID id, TreeMap<Characteristic, Integer> keyCharsImpactMap) {
        this(title, 1, 0.0f, id, keyCharsImpactMap);
    }

    public Skill(String title, int level, double sublevel, UUID id, TreeMap<Characteristic, Integer> keyCharsImpactMap) {
        this.title = title;
        this.level = level;
        this.sublevel = sublevel;
        this.keyCharsImpactMap = keyCharsImpactMap;
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

    public TreeMap<Characteristic, Integer> getKeyCharacteristicsMap() {
        return keyCharsImpactMap;
    }

    public void setKeyCharacteristicsMap(TreeMap<Characteristic, Integer> keyCharsImpactMap) {
        this.keyCharsImpactMap = keyCharsImpactMap;
    }

    public void addKeyCharacteristic(Characteristic characteristic, int impact){
        if (characteristic != null && !keyCharsImpactMap.containsKey(characteristic)) {
            keyCharsImpactMap.put(characteristic, impact);
        }
    }

    public void removeKeyCharacteristic(Characteristic characteristic){
        if (characteristic != null) {
            keyCharsImpactMap.remove(characteristic);
        }
    }

    public void removeAllKeyCharacteristics() {
        keyCharsImpactMap.clear();
    }

    public String getKeyCharacteristicsStringForDB() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Characteristic, Integer> pair : keyCharsImpactMap.entrySet()) {
            sb.append(pair.getKey().getId().toString())
                    .append(CHAR_IMPACT_DB_DIVIDER)
                    .append(pair.getValue())
                    .append(CHAR_CHAR_DB_DIVIDER);
        }
        return sb.toString();
    }

    /**
     * @return true if level changed
     */
    public boolean increaseSublevel(double value){
        sublevel += value;
        while (sublevel >= ((double)level)){
            sublevel = sublevel - ((double) level);
            level++;
            for (Characteristic ch : getKeyCharacteristicsMap().keySet()) {
                ch.increaseLevelByN(getActualGrowthForCharacteristic(ch));
            }
            if (sublevel <(double)level) { return true; }
        }
        return false;
    }

    /**
     * @return true if level changed
     */
    public boolean decreaseSublevel(double value){
        sublevel -= value;
        while (sublevel < 0.0d && level > 1) {
            for (Characteristic ch : getKeyCharacteristicsMap().keySet()) {
                ch.increaseLevelByN(-getActualGrowthForCharacteristic(ch));
            }
            level --;
            sublevel = ((double) level) + sublevel;
            if (sublevel < 0 && level <= 1) {
                sublevel = 0;
                level = 1;
            }
            if (sublevel > 0.0d) { return true; }
        }
        if (sublevel < 0) { sublevel = 0; }
        return false;
    }

    private int getActualGrowthForCharacteristic(Characteristic characteristic) {
        int initialGrowth = getKeyCharacteristicsGrowth();
        int impact = 100;
        if (getKeyCharacteristicsMap() != null || getKeyCharacteristicsMap().get(characteristic) != null) {
            impact = getKeyCharacteristicsMap().get(characteristic);
        }
        double impactMultiplier = impact / 100d;
        double actualGrowth = initialGrowth * impactMultiplier;
        if (actualGrowth > 0 && actualGrowth <= 1) {
            actualGrowth = 1;
        } else {
            actualGrowth = Math.round(actualGrowth);
        }
        return (int) actualGrowth;
    }

    public int getKeyCharacteristicsGrowth() {
        return 1 + level / 5;
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

    @Override
    public int compareTo(Skill another) {
        return new SkillByLevelComparator().compare(this, another);
    }

    public void reset() {
        level = 1;
        sublevel = 0;
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
            } else if ((int)rhs.getSublevel() != (int)lhs.getSublevel()) {
                return (int)(rhs.getSublevel() - lhs.getSublevel());
            } else {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        }
    }
}
