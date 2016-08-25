package com.levor.liferpgtasks.view.fragments.rewards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.RewardsController;
import com.levor.liferpgtasks.model.Reward;
import com.levor.liferpgtasks.view.fragments.DataDependantFrament;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditRewardFragment extends DataDependantFrament {
    public static String CURRENT_REWARD_TAG = "current_reward_tag";

    private Reward currentReward;
    private RewardsController rewardsController;
    private int rewardMode;

    @Bind(R.id.reward_title_edit_text)          EditText titleEditText;
    @Bind(R.id.reward_description_edit_text)    EditText descriptionEditText;
    @Bind(R.id.reward_cost_edit_text)           EditText costEditText;
    @Bind(R.id.mode_layout)                     View modeLayout;
    @Bind(R.id.reward_repeat_mode_text_view)    TextView modeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_reward, container, false);
        ButterKnife.bind(this, v);

        rewardsController = RewardsController.getInstance(getCurrentActivity());

        if (getArguments() != null) {
            currentReward = rewardsController.getRewardByID((UUID) getArguments().get(CURRENT_REWARD_TAG));
            titleEditText.setText(currentReward.getTitle());
            descriptionEditText.setText(currentReward.getDescription());
            costEditText.setText(String.valueOf(currentReward.getCost()));
            rewardMode = currentReward.getMode();
        } else {
            rewardMode = Reward.Mode.SINGLE_TIME;
        }

        updateModeView();

        modeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showSoftKeyboard(false, getView());
                String[] notifyVariants = getResources().getStringArray(R.array.rewards_mode_array);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.select_dialog_item, notifyVariants);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rewardMode = which;
                        updateModeView();
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(currentReward != null ?
                currentReward.getTitle()
                : getString(R.string.add_new_reward));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Edit reward Fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getView() != null) {
            getCurrentActivity().showSoftKeyboard(false, getView());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_reward, menu);

        if (currentReward == null) {
            MenuItem item = menu.findItem(R.id.remove_menu_item);
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ok_menu_item:
                if (titleEditText.getText().toString().equals("")){
                    Toast.makeText(getContext(), getString(R.string.empty_reward_title_error), Toast.LENGTH_SHORT).show();
                } else if (currentReward == null &&
                        rewardsController.getRewardByTitle(titleEditText.getText().toString()) != null){
                    Toast.makeText(getContext(), getString(R.string.duplicate_reward_title_error), Toast.LENGTH_SHORT).show();
                } else if (costEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), getString(R.string.reward_cost_empty_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (currentReward == null) {
                        currentReward = new Reward(titleEditText.getText().toString());
                        currentReward.setTitle(titleEditText.getText().toString());
                        currentReward.setDescription(descriptionEditText.getText().toString());
                        currentReward.setCost(Integer.parseInt(costEditText.getText().toString()));
                        currentReward.setMode(rewardMode);
                        rewardsController.addReward(currentReward);
                    } else {
                        currentReward.setTitle(titleEditText.getText().toString());
                        currentReward.setDescription(descriptionEditText.getText().toString());
                        currentReward.setCost(Integer.parseInt(costEditText.getText().toString()));
                        currentReward.setMode(rewardMode);
                        rewardsController.updateReward(currentReward);
                    }
                    getCurrentActivity().showPreviousFragment();
                }
                return true;
            case R.id.remove_menu_item:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(currentReward.getTitle())
                        .setMessage(getString(R.string.removing_reward_message))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rewardsController.removeReward(currentReward);
                                getCurrentActivity().showNthPreviousFragment(2);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateModeView();
    }

    private void updateModeView() {
        modeTextView.setText(rewardMode == Reward.Mode.SINGLE_TIME ? R.string.reward_single_repeat_mode :
                R.string.reward_infinite_repeat_mode);
    }

    @Override
    public boolean isDependableDataAvailable() {
        if (getArguments() == null) return false;
        UUID id = (UUID)getArguments().get(CURRENT_REWARD_TAG);
        Reward reward = RewardsController.getInstance(getCurrentActivity()).getRewardByID(id);
        return reward != null;
    }
}
