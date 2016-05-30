package com.levor.liferpgtasks.view.fragments.skills;

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

import com.levor.liferpgtasks.adapters.SimpleRecyclerAdapter;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.characteristics.CharacteristicsChartFragment;
import com.levor.liferpgtasks.view.fragments.characteristics.DetailedCharacteristicFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillsFragment extends DefaultFragment {
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skills, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.skills_recycler_view);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_skills_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_chart:
                getCurrentActivity().showChildFragment(new SkillsChartFragment(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Skills Fragment");
    }

    @Override
    public void updateUI() {
        List<Skill> skills = getController().getAllSkills();
        List<String> rows = new ArrayList<>(skills.size());
        DecimalFormat df = new DecimalFormat("#.##");
        for (Skill sk : skills) {
            StringBuilder sb = new StringBuilder();
            sb.append(sk.getTitle())
                    .append(" - ")
                    .append(sk.getLevel())
                    .append("(")
                    .append(df.format(sk.getSublevel()))
                    .append(")");
            rows.add(sb.toString());
        }
        SimpleRecyclerAdapter adapter = new SimpleRecyclerAdapter(rows, getCurrentActivity());
        adapter.registerOnItemClickListener(new SimpleRecyclerAdapter.OnRecycleItemClickListener(){
            @Override
            public void onItemClick(int position) {
                UUID currentId = getController().getAllSkills().get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG
                        , currentId);
                DefaultFragment fragment = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(fragment, bundle);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getCurrentActivity()));
    }
}
