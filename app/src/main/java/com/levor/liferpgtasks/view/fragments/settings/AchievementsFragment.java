package com.levor.liferpgtasks.view.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.levor.liferpgtasks.AchievsList;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.TwoStringsRowAdapter;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.List;

public class AchievementsFragment extends DefaultFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_achievements, container, false);
        ListView availableListView = (ListView) v.findViewById(R.id.available_achievements_list_view);
        availableListView.setAdapter(new TwoStringsRowAdapter(getCurrentActivity(), getAvailableAchievementsList()));
        availableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putInt(DetailedAchievementsFragment.ACHIEVEMNT_ORDINAL_TAG, position);
                getCurrentActivity().showChildFragment(new DetailedAchievementsFragment(), b);
            }
        });
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
        return list;
    }

    private void addAchievementToList(List<String[]> list, AchievsList achievement, int level){
        list.add(new String[]
                {String.format(achievement.getDescription(), achievement.getThresholdForLevel(level)),
                        getString(R.string.xp_multiplier_reward, achievement.getReward())});
    }
}
