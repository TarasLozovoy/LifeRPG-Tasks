package com.levor.liferpgtasks.view.fragments.rewards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
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

    private RewardsController rewardsController;

    private int filter;
    private RecyclerView recyclerView;
    private TextView emptyList;
    private List<String> sortedRewardsTitles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rewardsController = RewardsController.getInstance(getCurrentActivity());

        View view = inflater.inflate(R.layout.fragment_filtered_list, container, false);
        filter = getArguments().getInt(FILTER_ARG);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewTasks);
        emptyList = (TextView) view.findViewById(R.id.empty_list);
        if (filter == CLAIMED) {
            emptyList.setText(R.string.empty_claimed_reward_list_view);
        } else {
            emptyList.setText(R.string.empty_rewards_list_view);
        }

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
//            registerForContextMenu(recyclerView);
            // TODO: 8/22/16 add context menu
        }
    }

    private int getSorting() {
        return ((RewardsFragment)getParentFragment()).getSorting();
    }

    private void setSorting(int sorting) {
        ((RewardsFragment)getParentFragment()).setSorting(sorting);
    }
}
