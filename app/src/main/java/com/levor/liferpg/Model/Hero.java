package com.levor.liferpg.Model;

public class Hero {
    private int level;
    private double xp;
    private double xpToNextLevel;
    private double baseXP;
    private String name;

    public Hero(){
        this(0, 0, 1, "Johnny");
    }

    public Hero(int level, double xp, double baseXP, String name){
        this.level = level;
        this.xp = xp;
        this.name = name;
        this.baseXP = baseXP;
        xpToNextLevel = xpToLevel(level);
    }

    public boolean increaseXP(double value){
        xp += value;
        return checkXPCeiling();
    }

    public boolean decreaseXP(double value){
        xp -= value;
        return checkXPCeiling();
    }

    public void setLevel(int level) {
        this.level = level;
        xpToNextLevel = xpToLevel(level);
        checkXPCeiling();
    }

    public void setXp(double xp) {
        this.xp = xp;
        checkXPCeiling();
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public double getXp() {
        return xp;
    }

    public double getBaseXP() {
        return baseXP;
    }

    public void setBaseXP(double baseXP) {
        this.baseXP = baseXP;
    }

    public double getXpToNextLevel() {
        return xpToNextLevel;
    }

    public String getName() {
        return name;
    }

    private double xpToLevel(int level){
        if (level < 0) return 0;
        return (10 + 10*Math.pow(level, 2));
    }

    private boolean checkXPCeiling(){
        if (xp >= xpToNextLevel){
            level++;
            xp = xp - xpToNextLevel;
            xpToNextLevel = xpToLevel(level);
            return true;
        }
        return false;
    }
}
