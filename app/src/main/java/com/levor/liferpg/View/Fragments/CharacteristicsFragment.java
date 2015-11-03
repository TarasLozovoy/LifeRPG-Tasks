package com.levor.liferpg.View.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpg.R;
import com.levor.liferpg.View.DetailedCharacteristicActivity;

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
                        , getController().getCharacteristicTitleAndLevelAsArray()[position].split(" ")[0]);
                Fragment f = new DetailedCharacteristicFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        getActivity().setTitle("Characteristics");
        return view;
    }

    @Override
    public void onResume() {
        createAdapter();
        super.onResume();
    }

    private void createAdapter(){
        String[] chars = getController().getCharacteristicTitleAndLevelAsArray();
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, chars));
    }
}
