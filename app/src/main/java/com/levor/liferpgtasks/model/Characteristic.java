package com.levor.liferpgtasks.model;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.UUID;

public class Characteristic implements Comparable<Characteristic>{
    private String title;
    private int level;
    private UUID id;

    private boolean updateNeeded;

    public static final Comparator<Characteristic> LEVEL_COMPARATOR = new CharacteristicByLevelComparator();
    public static final Comparator<Characteristic> TITLE_COMPARATOR = new CharacteristicByNameComparator();

    public Characteristic(String title, int level) {
        this(title, level, UUID.randomUUID());
    }

    public Characteristic(String title, int level, UUID id) {
        this.title = title;
        this.level = level;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    public void increaseLevelByN(int N){
        level += N;
    }

    @Override
    public boolean equals(Object o) {
        Characteristic ch;
        if (o instanceof Characteristic){
            ch = (Characteristic) o;
            return this.title.equals(ch.title);
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Characteristic another) {
        if (this.level < another.level){
            return +1;
        } else if ( this.level > another.level){
            return -1;
        }
        return this.title.compareTo(another.title);
    }

    private static class CharacteristicByLevelComparator implements Comparator<Characteristic> {

        @Override
        public int compare(Characteristic lhs, Characteristic rhs) {
            if (rhs.getLevel() != lhs.getLevel()) {
                return rhs.getLevel() - lhs.getLevel();
            } else {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        }
    }

    private static class CharacteristicByNameComparator implements Comparator<Characteristic> {

        @Override
        public int compare(Characteristic lhs, Characteristic rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }
}
