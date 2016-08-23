package com.levor.liferpgtasks.view.fragments.rewards;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.RewardsController;
import com.levor.liferpgtasks.model.Reward;
import com.levor.liferpgtasks.view.fragments.DataDependantFrament;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailedRewadsFragment extends DataDependantFrament {
    public final static String SELECTED_REWARD_UUID_TAG = "selected_reward_uuid_tag";

    @Bind(R.id.reward_title) TextView rewardTitleTV;
    @Bind(R.id.reward_cost) TextView rewardCostTV;
    @Bind(R.id.reward_description) TextView rewardDescriptionTV;

    private Reward currentReward;
    private RewardsController rewardsController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailed_reward, container, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        ButterKnife.bind(this, v);

        rewardsController = RewardsController.getInstance(getCurrentActivity());

        UUID id = (UUID)getArguments().get(SELECTED_REWARD_UUID_TAG);
        currentReward = rewardsController.getRewardByID(id);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable(EditRewardFragment.CURRENT_REWARD_TAG, currentReward.getId());
                DefaultFragment f = new EditRewardFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });

        rewardTitleTV.setText(currentReward.getTitle());
        rewardCostTV.setText(getString(R.string.cost) + " " + String.valueOf(currentReward.getCost()));
        rewardDescriptionTV.setText(currentReward.getDescription());

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.reward));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_detailed_reward, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.claim_reward).setVisible(!currentReward.isDone())
                .setEnabled(!currentReward.isDone());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.claim_reward:
                rewardsController.claimReward(currentReward);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Detailed reward Fragment");
    }

    @Override
    public boolean isDependableDataAvailable() {
        return true;
        // TODO: 8/23/16 add check from RewardController on null
    }
}
