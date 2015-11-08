package com.levor.liferpg.Model;

public class Characteristic implements Comparable<Characteristic>{
    private String title;
    private int level;

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
    public int compareTo(Characteristic another) {
        if (this.level < another.level){
            return +1;
        } else if ( this.level > another.level){
            return -1;
        }
        return this.title.compareTo(another.title);
    }
}
