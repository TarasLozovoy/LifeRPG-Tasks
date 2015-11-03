package com.levor.liferpg.View.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.R;

import java.util.ArrayList;
import java.util.List;

public class SkillsFragment extends DefaultFragment {
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skills, container, false);
        listView = (ListView) view.findViewById(R.id.skills_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailedSkillFragment.SELECTED_SKILL_TITLE_TAG
                        , getController().getAllSkills().get(position).getTitle());
                Fragment fragment = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(fragment, bundle);
            }
        });
        getActivity().setTitle("Skills");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        updateAdapter();
        super.onResume();
    }

    private void updateAdapter() {
        List<Skill> skills = getController().getAllSkills();
        List<String> rows = new ArrayList<>(skills.size());
        for (Skill sk : skills) {
            StringBuilder sb = new StringBuilder();
            sb.append(sk.getTitle())
                    .append(" - ")
                    .append(sk.getLevel())
                    .append("(")
                    .append(sk.getSublevel())
                    .append(")");
            rows.add(sb.toString());
        }
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, rows.toArray(new String[rows.size()])));
    }
}
