package com.levor.liferpgtasks.view.fragments.characteristics;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.internal.NavigationMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.SimpleRecyclerAdapter;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.MainFragment;

import java.util.List;

public class CharacteristicsFragment extends DefaultFragment {
    private static final int EDIT_CONTEXT_MENU_ITEM = 1;
    private static final int DELETE_CONTEXT_MENU_ITEM = 2;

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characteristics, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Characteristics Fragment");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_characteristics_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_chart:
                getCurrentActivity().showChildFragment(new CharacteristicsChartFragment(), null);
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
        if (v.getId() == R.id.recycler_view){
            String[] characteristicsTitles = getController().getCharacteristicsTitleAndLevelAsArray();
            int selectedIndex = ((SimpleRecyclerAdapter)recyclerView.getAdapter()).getPosition();
            String selectedTitle = characteristicsTitles[selectedIndex].split(" - ")[0];
            menu.setHeaderTitle(selectedTitle);
            menu.add(MainFragment.CHARACTERISTICS_FRAGMENT_ID, EDIT_CONTEXT_MENU_ITEM, EDIT_CONTEXT_MENU_ITEM, R.string.edit_task);
            menu.add(MainFragment.CHARACTERISTICS_FRAGMENT_ID, DELETE_CONTEXT_MENU_ITEM, DELETE_CONTEXT_MENU_ITEM, R.string.remove);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == MainFragment.CHARACTERISTICS_FRAGMENT_ID) {
            String[] characteristicsTitles = getController().getCharacteristicsTitleAndLevelAsArray();
            int selectedIndex = ((SimpleRecyclerAdapter)recyclerView.getAdapter()).getPosition();
            String selectedTitle = characteristicsTitles[selectedIndex].split(" - ")[0];
            final Characteristic selectedCharacteristic = getController().getCharacteristicByTitle(selectedTitle);

            int menuItemIndex = item.getItemId();
            switch (menuItemIndex) {
                case EDIT_CONTEXT_MENU_ITEM:
                    DefaultFragment f = new EditCharacteristicFragment();
                    Bundle b = new Bundle();
                    b.putSerializable(EditCharacteristicFragment.CHARACTERISTIC_TAG, selectedCharacteristic.getId());
                    getCurrentActivity().showChildFragment(f, b);
                    return true;
                case DELETE_CONTEXT_MENU_ITEM:
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(selectedCharacteristic.getTitle())
                            .setMessage(getString(R.string.removing_characteristic_message))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getController().removeCharacteristic(selectedCharacteristic);
                                    dialog.dismiss();
                                    updateUI();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .show();
                    return true;
            }
        }
        return false;
    }

    @Override
    public void updateUI(){
        String[] chars = getController().getCharacteristicsTitleAndLevelAsArray();
        SimpleRecyclerAdapter adapter = new SimpleRecyclerAdapter(chars, getCurrentActivity());
        adapter.registerOnItemClickListener(new SimpleRecyclerAdapter.OnRecycleItemClickListener(){
            @Override
            public void onItemClick(int position) {
                Bundle b = new Bundle();
                String charTitle = getController().getCharacteristics().get(position).getTitle();
                b.putSerializable(DetailedCharacteristicFragment.CHARACTERISTIC_ID
                        , getController().getCharacteristicByTitle(charTitle).getId());
                DefaultFragment f = new DetailedCharacteristicFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getCurrentActivity()));
        registerForContextMenu(recyclerView);
    }


}
