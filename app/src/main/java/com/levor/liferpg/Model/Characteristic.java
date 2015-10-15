package com.levor.liferpg.Model;

public class Characteristic {
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
}
