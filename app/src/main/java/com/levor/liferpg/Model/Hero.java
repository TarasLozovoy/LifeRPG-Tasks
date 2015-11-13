package com.levor.liferpg.Model;

public class Hero {
    private int level;
    private int xp;
    private int xpToNextLevel;
    private String name;

    public Hero(){
        this(0, 0, "Johnny");
    }

    public Hero(int level, int xp, String name){
        this.level = level;
        this.xp = xp;
        this.name = name;
        xpToNextLevel = xpToLevel(level);
    }

    /**
     * Increases xp. When xp is reached to xpToNextLevelValue - increases hero level.
     * @return true - if level increased, false - if not.
     */
    public boolean increaseXP(){
        xp++;
        return checkXPCeiling();
    }

    /**
     * Decreases xp. When xp below xp for current level - decreases hero level.
     * @return true - if level decreased, false - if not.
     */
    public boolean decreaseXP(){
        xp--;
        if (xp < xpToLevel(level - 1)){
            level--;
            xpToNextLevel = xpToLevel(level);
            return true;
        }
        return false;
    }

    public void setLevel(int level) {
        this.level = level;
        xpToNextLevel = xpToLevel(level);
        checkXPCeiling();
    }

    public void setXp(int xp) {
        this.xp = xp;
        checkXPCeiling();
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    public String getName() {
        return name;
    }

    private int xpToLevel(int level){
        if (level < 0) return 0;
        return (int) (10 + 10*Math.pow(level, 2));
    }

    private boolean checkXPCeiling(){
        if (xp == xpToNextLevel){
            level++;
            xp = 0;
            xpToNextLevel = xpToLevel(level);
            return true;
        }
        return false;
    }
}
