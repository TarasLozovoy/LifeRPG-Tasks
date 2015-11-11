package com.levor.liferpg.View.Fragments.Characteristics;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;

/**
 * A simple {@link Fragment} subclass.
 */
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
                b.putSerializable(DetailedCharacteristicFragment.CHARACTERISTIC_TITLE
                        , getController().getCharacteristicsTitleAndLevelAsArray()[position].split(" ")[0]);
                Fragment f = new DetailedCharacteristicFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        getCurrentActivity().setActionBarTitle("Characteristics");
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return view;
    }

    @Override
    public void onResume() {
        createAdapter();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAdapter(){
        String[] chars = getController().getCharacteristicsTitleAndLevelAsArray();
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, chars));
    }
}
