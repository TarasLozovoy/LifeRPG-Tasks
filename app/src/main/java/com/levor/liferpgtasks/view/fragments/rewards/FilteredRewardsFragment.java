package com.levor.liferpgtasks.view.fragments.rewards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.TextUtils;
import com.levor.liferpgtasks.adapters.RewardsAdapter;
import com.levor.liferpgtasks.controller.RewardsController;
import com.levor.liferpgtasks.model.Reward;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilteredRewardsFragment extends DefaultFragment {
    public static final String FILTER_ARG = "filter_reward_arg";

    public static final int NEW = 0;
    public static final int CLAIMED = 1;
    private static final int UNDO_CONTEXT_MENU_ITEM = 0;
    private static final int EDIT_CONTEXT_MENU_ITEM = 1;
    private static final int DELETE_CONTEXT_MENU_ITEM = 2;

    private RewardsController rewardsController;

    private int filter;
    private RecyclerView recyclerView;
    private TextView emptyList;
    private TextView moneyTextView;
    private View moneyView;
    private List<String> sortedRewardsTitles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rewardsController = RewardsController.getInstance(getCurrentActivity());

        View view = inflater.inflate(R.layout.fragment_filtered_list, container, false);
        filter = getArguments().getInt(FILTER_ARG);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyList = (TextView) view.findViewById(R.id.empty_list);
        moneyTextView = (TextView) view.findViewById(R.id.money);
        moneyView =  view.findViewById(R.id.money_layout);
        if (filter == CLAIMED) {
            emptyList.setText(R.string.empty_claimed_reward_list_view);
        } else {
            emptyList.setText(R.string.empty_rewards_list_view);
        }

        updateTotalGold();
        setupListView();
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(R.string.rewards);
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        isCreated = true;
        return view;
    }

    @Override
    public void updateUI() {
        setupListView();
        updateTotalGold();
    }

    private void updateFilteredFragmentsUI(){
        ((RewardsFragment)getParentFragment()).updateChildFragmentsUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sorting:
                String[] sortingVariants = getResources().getStringArray(R.array.rewards_sorting_spinner_items);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.select_dialog_item, sortingVariants);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getSorting() != which) {
                            setSorting(which);
                            updateFilteredFragmentsUI();
                        }
                        dialog.dismiss();
                    }
                });
                String currentSorting = getString(R.string.current_sorting) + "\n" + sortingVariants[getSorting()];
                dialog.setTitle(currentSorting);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recyclerView != null) {
            unregisterForContextMenu(recyclerView);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.recycler_view) {
            int selectedIndex = ((RewardsAdapter) recyclerView.getAdapter()).getPosition();
            String selectedTitle = sortedRewardsTitles.get(selectedIndex);
            Reward reward = rewardsController.getRewardByTitle(selectedTitle);
            menu.setHeaderTitle(selectedTitle);
            if (reward.isDone()) {
                menu.add(filter, UNDO_CONTEXT_MENU_ITEM, UNDO_CONTEXT_MENU_ITEM, R.string.undo);
            }
            menu.add(filter, EDIT_CONTEXT_MENU_ITEM, EDIT_CONTEXT_MENU_ITEM, R.string.edit_task);
            menu.add(filter, DELETE_CONTEXT_MENU_ITEM, DELETE_CONTEXT_MENU_ITEM, R.string.remove);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == filter) {
            int selectedIndex = ((RewardsAdapter) recyclerView.getAdapter()).getPosition();
            String selectedTitle = sortedRewardsTitles.get(selectedIndex);
            final Reward reward = rewardsController.getRewardByTitle(selectedTitle);

            int menuItemIndex = item.getItemId();
            switch (menuItemIndex) {
                case UNDO_CONTEXT_MENU_ITEM:
                    rewardsController.unclaim(reward);
                    updateFilteredFragmentsUI();
                    return true;
                case EDIT_CONTEXT_MENU_ITEM:
                    DefaultFragment f = new EditRewardFragment();
                    Bundle b = new Bundle();
                    b.putSerializable(EditRewardFragment.CURRENT_REWARD_UUID_TAG, reward.getId());
                    getCurrentActivity().showChildFragment(f, b);
                    return true;
                case DELETE_CONTEXT_MENU_ITEM:
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(reward.getTitle())
                            .setMessage(getString(R.string.removing_reward_message))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    rewardsController.removeReward(reward);
                                    dialog.dismiss();
                                    updateFilteredFragmentsUI();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .show();
                    return true;
            }
        }
        return false;
    }

    private void setupListView() {
        List<Reward> rewards = rewardsController.getAllRewards();
        Comparator<Reward> comparator = null;
        switch (getSorting()){
            case Reward.SortingOrder.TITLE_ASC :
                comparator = Reward.TITLE_ASC_REWARDS_COMPARATOR;
                break;
            case Reward.SortingOrder.TITLE_DESC :
                comparator = Reward.TITLE_DESC_REWARDS_COMPARATOR;
                break;
        }
        Collections.sort(rewards, comparator);
        sortedRewardsTitles = new ArrayList<>();

        for (Reward r : rewards) {
            switch (filter) {
                case NEW:
                    if (!r.isDone()) {
                        sortedRewardsTitles.add(r.getTitle());
                    }
                    break;
                case CLAIMED:
                    if (r.isDone()) {
                        sortedRewardsTitles.add(r.getTitle());
                    }
                    break;
                default:
                    throw new RuntimeException("Not supported filter.");
            }
        }

        if (sortedRewardsTitles.isEmpty()) {
            emptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            RewardsAdapter adapter = new RewardsAdapter(sortedRewardsTitles, getCurrentActivity());
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    updateFilteredFragmentsUI();
                    super.onChanged();
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            registerForContextMenu(recyclerView);
        }
    }

    private void updateTotalGold() {
        if (filter == NEW) {
            moneyView.setVisibility(View.VISIBLE);
            double money = getController().getHero().getMoney();
            String moneyString = getString(R.string.total) + " " + TextUtils.DECIMAL_FORMAT.format(money);
            moneyTextView.setText(moneyString);
        } else {
            moneyView.setVisibility(View.GONE);
        }
    }

    private int getSorting() {
        return ((RewardsFragment)getParentFragment()).getSorting();
    }

    private void setSorting(int sorting) {
        ((RewardsFragment)getParentFragment()).setSorting(sorting);
    }
}
