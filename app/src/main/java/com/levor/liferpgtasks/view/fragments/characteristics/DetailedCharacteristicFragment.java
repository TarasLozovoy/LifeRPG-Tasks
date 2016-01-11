package com.levor.liferpgtasks.view.fragments.characteristics;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.skills.AddSkillFragment;
import com.levor.liferpgtasks.view.fragments.skills.DetailedSkillFragment;

import java.util.ArrayList;

public class DetailedCharacteristicFragment extends DefaultFragment {
    public final static String CHARACTERISTIC_TITLE = "characteristic_title";

    private TextView levelValue;
    private TextView characteristicTitle;
    private ListView listView;

    private Characteristic currentCharacteristic;
    private ArrayList<Skill> currentSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detailed_characteristic, container, false);
        currentCharacteristic = getController().getCharacteristicByTitle(getArguments().getString(CHARACTERISTIC_TITLE));
        getCurrentActivity().setActionBarTitle(currentCharacteristic.getTitle() + " details");

        levelValue = (TextView) v.findViewById(R.id.level_value);
        characteristicTitle = (TextView) v.findViewById(R.id.characteristic_title);
        listView = (ListView) v.findViewById(R.id.list_view);
        characteristicTitle.setText(currentCharacteristic.getTitle());
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        levelValue.setText("" + currentCharacteristic.getLevel());
        createFooterView();
        createAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG, currentSkills.get(position).getId());
                DefaultFragment f = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Detailed Characteristic Fragment");
    }

    private void createFooterView() {
        Button footerButton = new Button(getActivity());
        footerButton.setText(R.string.create_new_skill);
        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultFragment f = new AddSkillFragment();
                Bundle b = new Bundle();
                b.putSerializable(AddSkillFragment.RECEIVED_CHARACTERISTIC_TITLE_TAG, currentCharacteristic.getTitle());
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        listView.addFooterView(footerButton);
    }

    private void createAdapter(){
        ArrayList<String> skills = new ArrayList<>();
        currentSkills = getController().getSkillsByCharacteristic(currentCharacteristic);
        for (Skill sk : currentSkills){
            skills.add(sk.getTitle() + " - " + sk.getLevel() + "(" + sk.getSublevel() + ")");
        }
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, skills));
    }
}
