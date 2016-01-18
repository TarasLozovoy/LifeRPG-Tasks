package com.levor.liferpgtasks.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpgtasks.AchievsList;
import com.levor.liferpgtasks.R;

public class DetailedAchievementsFragment extends DefaultFragment {
    public static final String ACHIEVEMNT_ORDINAL_TAG = "achievement_ordinal_tag";
    private AchievsList achievement;
    private int level;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailed_achievements, container, false);
        ListView listView = (ListView) v;
        achievement = AchievsList.values()[getArguments().getInt(ACHIEVEMNT_ORDINAL_TAG)];
        level = getController().getAchievementsLevels().get(achievement.ordinal());
        listView.setAdapter(new AchievesAdapter());
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.achievements));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    private class AchievesAdapter extends BaseAdapter implements ListAdapter {

        @Override
        public int getCount() {
            return level + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return level + 1 - position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null){
                LayoutInflater inflater =
                        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.detailed_achieve_list_item, null);
            }
            TextView description = (TextView) view.findViewById(R.id.item_1);
            TextView reward = (TextView) view.findViewById(R.id.item_2);
            ImageView doneImage = (ImageView) view.findViewById(R.id.achiev_unlocked_image);

            String descString = String.format(achievement.getDescription(),
                    achievement.getThresholdForLevel(level - position));
            String rewardString = getString(R.string.xp_multiplier_reward, achievement.getReward());
            description.setText(descString);
            reward.setText(rewardString);
            doneImage.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }
}