package com.levor.liferpgtasks.view.fragments.characteristics;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

public class CharacteristicsFragment extends DefaultFragment {
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
    }


}
