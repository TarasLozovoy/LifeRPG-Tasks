package com.levor.liferpgtasks.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.TwoStringsRowAdapter;

import java.util.ArrayList;
import java.util.List;

public class AchievementsFragment extends DefaultFragment {
    private ListView availableListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_achievements, container, false);
        availableListView = (ListView) v.findViewById(R.id.available_achievements_list_view);
        availableListView.setAdapter(new TwoStringsRowAdapter(getCurrentActivity(), getAvailableAchievementsList()));

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.achievements));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    public List<String[]> getAvailableAchievementsList() {
        List<String[]> list = new ArrayList<>();

        list.add(new String[]
                {getString(R.string.total_hero_xp_achievement, AchievsList.TOTAL_HERO_XP.getThresholdForPosition(0)),
                        getString(R.string.xp_multiplier_reward, AchievsList.TOTAL_HERO_XP.getReward())});
        list.add(new String[]
                {getString(R.string.total_skills_xp_achievement, AchievsList.TOTAL_SKILLS_XP.getThresholdForPosition(0)),
                        getString(R.string.xp_multiplier_reward, AchievsList.TOTAL_SKILLS_XP.getReward())});
        list.add(new String[]
                {getString(R.string.performed_tasks_achievement, AchievsList.PERFORMED_TASKS.getThresholdForPosition(0)),
                        getString(R.string.xp_multiplier_reward, AchievsList.PERFORMED_TASKS.getReward())});
        list.add(new String[]
                {getString(R.string.hero_level_achievement, AchievsList.HERO_LEVEL.getThresholdForPosition(1)),
                getString(R.string.xp_multiplier_reward, AchievsList.HERO_LEVEL.getReward())});
        return list;
    }

    enum AchievsList {
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

        private List<Long> count = new ArrayList<>();
        private int maxCount = 10000;
        private int reward;

        AchievsList(int difficulty) {
            switch (difficulty) {
                case 0:
                    for (int i = 0; i < maxCount; i++) {
                        if (i == 0){
                            count.add(10L);
                            continue;
                        }
                        count.add((long) (i * 100 * i));
                    }
                    reward = 1;
                    break;
                case 1:
                    for (int i = 0; i < maxCount; i++) {
                        if (i == 0){
                            count.add(1L);
                            continue;
                        }
                        count.add((long) (i * 5 * (i + 1)));
                    }
                    reward = 1;
                    break;
                case 2:
                    for (int i = 0; i < maxCount; i++) {
                        if (i == 0){
                            count.add(5L);
                            continue;
                        }
                        count.add((long) (i * 5 + 5));
                    }
                    reward = 2;
                    break;
                case 3:
                    for (int i = 0; i < maxCount; i++) {
                        if (i == 0){
                            count.add(2L);
                            continue;
                        }
                        count.add((long) (i * 5));
                    }
                    reward = 3;
                    break;
            }
        }

        public int getReward() {
            return reward;
        }

        public long getThresholdForPosition(int position){
            return count.get(position);
        }
    }
}
