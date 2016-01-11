package com.levor.liferpgtasks;

public enum AchievsList {
    TOTAL_HERO_XP(0),
    TOTAL_SKILLS_XP(0),
    PERFORMED_TASKS(1),
    FINISHED_TASKS(1),
    ADDED_TASKS(1),
    TOP_LEVEL_SKILL(2),
    TOP_LEVEL_CHARACTERISTIC(2),
    HERO_LEVEL(3),
    NUMBER_OF_SKILLS_WITH_LEVEL_10(3),
    NUMBER_OF_SKILLS_WITH_LEVEL_25(3),
    NUMBER_OF_SKILLS_WITH_LEVEL_50(3),
    NUMBER_OF_SKILLS_WITH_LEVEL_100(3);

    private int difficulty;
    private String description;

    AchievsList(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getReward() {
        switch (difficulty) {
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 1;
        }
    }

    public long getThresholdForLevel(int position){
        switch (difficulty) {
            case 0:
                if (position == 0){
                    return 10;
                }
                return (long) (position * position * 100);
            case 1:
                if (position == 0){
                    return 1;
                }
                return (position * (position + 1) * 5);
            case 2:
                if (position == 0){
                    return 5;
                }
                return (long) (position * 5 + 5);
            case 3:
                if (position == 0){
                    return 2;
                }
                return (long) (position * 5);
            default:
                throw new RuntimeException("Value is not permitted!");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
