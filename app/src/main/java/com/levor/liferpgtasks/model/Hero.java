package com.levor.liferpgtasks.model;

public class Hero {
    private int level;
    private double xp;
    private double xpToNextLevel;
    private double baseXP;
    private String name;
    private double money;

    public Hero(){
        this(0, 0, 1, "Johnny", 0);
    }

    public Hero(int level, double xp, double baseXP, String name, double money){
        this.level = level;
        this.xp = xp;
        this.name = name;
        this.baseXP = baseXP;
        this.money = money;
        xpToNextLevel = xpToLevel(level);
    }

    public boolean addXP(double value){
        xp += value;
        return checkXPFloor() || checkXPCeiling();
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

    public double getTotalXP(){
        double total = 0;
        for (int i = 1; i <= level; i++) {
            total += xpToLevel(i);
        }
        return total;
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

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void addMoney(double amount) {
        this.money += amount;
    }

    public void removeMoney(double amount) {
        this.money -= amount;
    }

    private double xpToLevel(int level){
        if (level < 0) return 0;
        return (10 + 5*Math.pow(level, 2));
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

    private boolean checkXPFloor(){
        if (xp < 0){
            level--;
            xpToNextLevel = xpToLevel(level);
            xp = xpToNextLevel + xp;
            if (level < 0) {
                level = 0;
                xp = 0;
                xpToNextLevel = xpToLevel(level);
            }
            return true;
        }
        return false;
    }

    public void reset() {
        level = 0;
        xp = 0;
        xpToNextLevel = xpToLevel(level);
        baseXP = 1.0;
    }
}
