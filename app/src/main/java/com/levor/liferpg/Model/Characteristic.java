package com.levor.liferpg.Model;

import android.support.annotation.NonNull;

import java.util.Comparator;

public class Characteristic implements Comparable<Characteristic>{
    private String title;
    private int level;

    public static final Comparator<Characteristic> LEVEL_COMPARATOR = new CharacteristicByLevelComparator();

    public Characteristic(String title, int level) {
        this.title = title;
        this.level = level;
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
}
