package com.levor.liferpgtasks.view.fragments.characteristics;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.levor.liferpgtasks.adapters.SimpleRecyclerAdapter;
import com.levor.liferpgtasks.adapters.TasksAdapter;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.skills.AddSkillFragment;
import com.levor.liferpgtasks.view.fragments.skills.DetailedSkillFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class DetailedCharacteristicFragment extends DefaultFragment {
    public final static String CHARACTERISTIC_ID = "characteristic_id";

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private View header;

    private Characteristic currentCharacteristic;
    private ArrayList<Skill> currentSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailed_characteristic, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        currentCharacteristic = getController().getCharacteristicByID((UUID) getArguments().get(CHARACTERISTIC_ID));

        header = inflater.inflate(R.layout.detailed_characteristics_header, null);
        TextView levelValue = (TextView) header.findViewById(R.id.level_value);
        TextView characteristicTitle = (TextView) header.findViewById(R.id.characteristic_title);
        Button addSkillButton = (Button) header.findViewById(R.id.add_skill_button);


        fab.setOnClickListener(new FabClickListener());

        characteristicTitle.setText(currentCharacteristic.getTitle());
        levelValue.setText(Integer.toString(currentCharacteristic.getLevel()));

        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable(AddSkillFragment.RECEIVED_CHARACTERISTIC_ID_TAG, currentCharacteristic.getId());
                getCurrentActivity().showChildFragment(new AddSkillFragment(), b);
            }
        });
        setupRecyclerView();

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(currentCharacteristic.getTitle());
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Detailed Characteristic Fragment");
    }

    private void setupRecyclerView(){
        ArrayList<String> skills = new ArrayList<>();
        currentSkills = getController().getSkillsByCharacteristic(currentCharacteristic);
        DecimalFormat df = new DecimalFormat("#.##");
        for (Skill sk : currentSkills){
            if (sk == null) {
                currentSkills.remove(sk);
                continue;
            }
            skills.add(sk.getTitle() + " - " + sk.getLevel() + "(" + df.format(sk.getSublevel()) + ")");
        }
        SimpleRecyclerAdapter adapter = new SimpleRecyclerAdapter(skills, getCurrentActivity());
        adapter.registerOnItemClickListener(new SimpleRecyclerAdapter.OnRecycleItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle b = new Bundle();
                b.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG,
                        currentSkills.get(position).getId());
                DefaultFragment f = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        adapter.setHeader(header);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getCurrentActivity()));
    }

    private class FabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();
            b.putSerializable(EditCharacteristicFragment.CHARACTERISTIC_TAG, currentCharacteristic.getId());
            getCurrentActivity().showChildFragment(new EditCharacteristicFragment(), b);
        }
    }
}
