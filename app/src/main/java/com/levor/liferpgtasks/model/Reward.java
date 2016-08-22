package com.levor.liferpgtasks.model;

import java.util.Comparator;
import java.util.Date;

public class Reward {
    public static final Comparator<Reward> TITLE_ASC_REWARDS_COMPARATOR = new TitleAscRewardsComparator();
    public static final Comparator<Reward> TITLE_DESC_REWARDS_COMPARATOR = new TitleDescRewardsComparator();

    // TODO: 8/22/16 remove mock title
    private String title = new Date().toString();
    private boolean isDone = false;

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
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
