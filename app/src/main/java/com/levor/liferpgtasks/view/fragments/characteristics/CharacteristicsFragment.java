package com.levor.liferpgtasks.view.fragments.characteristics;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

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
                DefaultFragment f = new DetailedCharacteristicFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        return view;
    }

    @Override
    protected void updateUI(){
        String[] chars = getController().getCharacteristicsTitleAndLevelAsArray();
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, chars));
    }
}
