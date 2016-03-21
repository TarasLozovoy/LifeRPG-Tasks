package com.levor.liferpgtasks.view.fragments.characteristics;


import android.os.Bundle;
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
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

public class CharacteristicsFragment extends DefaultFragment {
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characteristics, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                String charTitle = getController().getCharacteristicsTitleAndLevelAsArray()[position].split(" ")[0];
                b.putSerializable(DetailedCharacteristicFragment.CHARACTERISTIC_ID
                        , getController().getCharacteristicByTitle(charTitle).getId());
                DefaultFragment f = new DetailedCharacteristicFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
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
            case R.id.add:
                getCurrentActivity().showChildFragment(new EditCharacteristicFragment(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateUI(){
        String[] chars = getController().getCharacteristicsTitleAndLevelAsArray();
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, chars));
    }
}
