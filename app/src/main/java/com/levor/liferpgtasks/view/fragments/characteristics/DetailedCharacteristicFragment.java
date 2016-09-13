package com.levor.liferpgtasks.view.fragments.characteristics;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.levor.liferpgtasks.Utils.TextUtils;
import com.levor.liferpgtasks.adapters.SimpleRecyclerAdapter;
import com.levor.liferpgtasks.adapters.TasksAdapter;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.MainFragment;
import com.levor.liferpgtasks.view.fragments.skills.AddSkillFragment;
import com.levor.liferpgtasks.view.fragments.skills.DetailedSkillFragment;
import com.levor.liferpgtasks.view.fragments.skills.EditSkillFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class DetailedCharacteristicFragment extends DefaultFragment {
    public final static String CHARACTERISTIC_ID = "characteristic_id";
    private static final int EDIT_CONTEXT_MENU_ITEM = 1;
    private static final int DELETE_CONTEXT_MENU_ITEM = 2;

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

    @Override
    public void onPause() {
        super.onPause();
        if (recyclerView != null) {
            unregisterForContextMenu(recyclerView);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.recycler_view) {
            int selectedIndex = ((SimpleRecyclerAdapter) recyclerView.getAdapter()).getPosition();
            String selectedTitle = currentSkills.get(selectedIndex).getTitle();
            menu.setHeaderTitle(selectedTitle);
            menu.add(0, EDIT_CONTEXT_MENU_ITEM, EDIT_CONTEXT_MENU_ITEM, R.string.edit_task);
            menu.add(0, DELETE_CONTEXT_MENU_ITEM, DELETE_CONTEXT_MENU_ITEM, R.string.remove);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int selectedIndex = ((SimpleRecyclerAdapter) recyclerView.getAdapter()).getPosition();
        final Skill skill = currentSkills.get(selectedIndex);

        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case EDIT_CONTEXT_MENU_ITEM:
                DefaultFragment f = new EditSkillFragment();
                Bundle b = new Bundle();
                b.putSerializable(EditSkillFragment.EDIT_SKILL_UUID_TAG, skill.getId());
                getCurrentActivity().showChildFragment(f, b);
                return true;
            case DELETE_CONTEXT_MENU_ITEM:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(skill.getTitle())
                        .setMessage(getString(R.string.removing_skill_message))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getController().removeSkill(skill);
                                dialog.dismiss();
                                setupRecyclerView();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
                return true;
        }
        return false;
    }

    private void setupRecyclerView() {
        ArrayList<String> skills = new ArrayList<>();
        currentSkills = getController().getSkillsByCharacteristic(currentCharacteristic);
        for (Skill sk : currentSkills) {
            if (sk == null) {
                currentSkills.remove(sk);
                continue;
            }
            skills.add(sk.getTitle() + " - " + sk.getLevel() + "(" + TextUtils.DECIMAL_FORMAT.format(sk.getSublevel()) + ")");
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
        registerForContextMenu(recyclerView);
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
