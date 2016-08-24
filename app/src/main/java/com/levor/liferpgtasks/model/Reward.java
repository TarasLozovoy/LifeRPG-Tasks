package com.levor.liferpgtasks.model;

import java.util.Comparator;
import java.util.UUID;

public class Reward {
    public static final Comparator<Reward> TITLE_ASC_REWARDS_COMPARATOR = new TitleAscRewardsComparator();
    public static final Comparator<Reward> TITLE_DESC_REWARDS_COMPARATOR = new TitleDescRewardsComparator();

    private String title;
    private int cost = 10000;
    private UUID id;
    private String description = "";
    private boolean isDone = false;

    public Reward (String title){
        this(title, UUID.randomUUID());
    }

    public Reward (String title, UUID id){
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class SortingOrder{
        public static final int TITLE_ASC = 0;
        public static final int TITLE_DESC = 1;
    }

    private static class TitleAscRewardsComparator implements Comparator<Reward> {

        @Override
        public int compare(Reward lhs, Reward rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }

    private static class TitleDescRewardsComparator implements Comparator<Reward> {

        @Override
        public int compare(Reward lhs, Reward rhs) {
            return rhs.getTitle().compareTo(lhs.getTitle());
        }
    }
}
