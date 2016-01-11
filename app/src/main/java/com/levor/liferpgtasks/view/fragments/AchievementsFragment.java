package com.levor.liferpgtasks.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.levor.liferpgtasks.AchievsList;
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
        for (int i = 0; i < getController().getAchievementsLevels().size(); i++) {
            addAchievementToList(list, AchievsList.values()[i], getController().getAchievementsLevels().get(i));
        }
//        addAchievementToList(list, TOTAL_HERO_XP, 1);
//        addAchievementToList(list, TOTAL_SKILLS_XP, 2);
//        addAchievementToList(list, PERFORMED_TASKS, 3);
//        addAchievementToList(list, FINISHED_TASKS, 4);
//        addAchievementToList(list, ADDED_TASKS, 5);
//        addAchievementToList(list, TOP_LEVEL_SKILL, 6);
//        addAchievementToList(list, TOP_LEVEL_CHARACTERISTIC, 7);
//        addAchievementToList(list, HERO_LEVEL, 8);
//        addAchievementToList(list, NUMBER_OF_SKILLS_WITH_LEVEL_10, 9);
//        addAchievementToList(list, NUMBER_OF_SKILLS_WITH_LEVEL_25, 10);
//        addAchievementToList(list, NUMBER_OF_SKILLS_WITH_LEVEL_50, 11);
//        addAchievementToList(list, NUMBER_OF_SKILLS_WITH_LEVEL_100, 12);

        return list;
    }

    private void addAchievementToList(List<String[]> list, AchievsList achievement, int level){
        list.add(new String[]
                {String.format(achievement.getDescription(), achievement.getThresholdForLevel(level)),
                        getString(R.string.xp_multiplier_reward, achievement.getReward())});
    }
}
