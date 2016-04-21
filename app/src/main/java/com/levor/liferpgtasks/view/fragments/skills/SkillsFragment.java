package com.levor.liferpgtasks.view.fragments.skills;

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

import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.characteristics.EditCharacteristicFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                UUID currentId = getController().getAllSkills().get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG
                        , currentId);
                DefaultFragment fragment = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(fragment, bundle);
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Skills Fragment");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getCurrentActivity() == null && !isVisibleToUser) return;
        getCurrentActivity().showFab(true);
        getCurrentActivity().setFabImage(R.drawable.ic_add_black_24dp);
        getCurrentActivity().setFabClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new AddSkillFragment(), null);
            }
        });
    }

    @Override
    public boolean isFabVisible() {
        return true;
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
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                rows.toArray(new String[rows.size()])));
    }
}
