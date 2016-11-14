package com.levor.liferpgtasks.view.fragments.skills;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.Dialogs.KeyCharacteristicsSelectionDialog;
import com.levor.liferpgtasks.view.fragments.DataDependantFrament;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class AddSkillFragment extends DataDependantFrament {
    public static final String RECEIVED_CHARACTERISTIC_ID_TAG = "received_characteristic_title_tag";

    protected final String SKILL_TITLE_TAG = "skill_title_tag";

    protected View relatedCharacteristicsView;
    protected EditText titleEditText;
    protected TextView relatedCharacteristicsTextView;

    protected TreeMap<UUID, Integer> keyCharacteristicsIdMap = new TreeMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_skill, container, false);
        titleEditText = (EditText) v.findViewById(R.id.new_skill_title_edit_text);
        relatedCharacteristicsTextView = (TextView) v.findViewById(R.id.related_characteristics_text_view);
        relatedCharacteristicsView = v.findViewById(R.id.add_characteristic_ll);

        relatedCharacteristicsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showSoftKeyboard(false, getView());
                KeyCharacteristicsSelectionDialog dialog = new KeyCharacteristicsSelectionDialog();
                Bundle b = new Bundle();
                b.putSerializable(KeyCharacteristicsSelectionDialog.CHARS_MAP, convertIdsToTitles());
                b.putBoolean(KeyCharacteristicsSelectionDialog.WITH_IMPACT, true);
                dialog.setArguments(b);
                dialog.setListener(new KeyCharacteristicsSelectionDialog.KeyCharacteristicsChangedListener() {
                    @Override
                    public void onChanged(TreeMap<String, Integer> characteristicsMap) {
                        convertTitlesToIds(characteristicsMap);
                        updateRelatedCharacteristicsView();
                    }
                });
                dialog.show(getCurrentActivity().getSupportFragmentManager(), "CharacteristicsSelection");
            }
        });

        UUID uuid;
        if (getArguments()!= null && (uuid = (UUID) getArguments().getSerializable(RECEIVED_CHARACTERISTIC_ID_TAG)) != null){
            keyCharacteristicsIdMap.put(uuid, 100);
        }
        if (savedInstanceState != null) {
            titleEditText.setText(savedInstanceState.getString(SKILL_TITLE_TAG));
        }
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getCurrentActivity().showSoftKeyboard(false, getView());
                }
            }
        });
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(R.string.new_skill);
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Add Skill Fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getView() != null) {
            getCurrentActivity().showSoftKeyboard(false, getView());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add_skill, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ok_menu_item:
                if (titleEditText.getText().toString().equals("")){
                    Toast.makeText(getContext(), getString(R.string.empty_skill_title_error), Toast.LENGTH_SHORT).show();
                } else if (keyCharacteristicsIdMap.isEmpty()){
                    Toast.makeText(getContext(), getString(R.string.no_key_characteristic_error), Toast.LENGTH_SHORT).show();
                } else if (getController().getSkillByTitle(titleEditText.getText().toString()) != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.oops))
                            .setMessage(getString(R.string.skill_duplicate_error))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish(titleEditText.getText().toString(), getString(R.string.skill_added_message));
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    getCurrentActivity().showSoftKeyboard(false, getView());
                    finish(titleEditText.getText().toString(), getString(R.string.skill_added_message));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SKILL_TITLE_TAG, titleEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private TreeMap<String, Integer> convertIdsToTitles() {
        TreeMap<String, Integer> titlesMap = new TreeMap<>();
        for (Map.Entry<UUID, Integer> pair : keyCharacteristicsIdMap.entrySet()) {
            titlesMap.put(getController().getCharacteristicByID(pair.getKey()).getTitle(), pair.getValue());
        }
        return titlesMap;
    }

    private void convertTitlesToIds(TreeMap<String, Integer> titlesMap) {
        keyCharacteristicsIdMap.clear();
        for (Map.Entry<String, Integer> pair : titlesMap.entrySet()) {
            keyCharacteristicsIdMap.put(getController().getCharacteristicByTitle(pair.getKey()).getId(), pair.getValue());
        }
    }

    private void updateRelatedCharacteristicsView(){
        StringBuilder sb = new StringBuilder();
        if (keyCharacteristicsIdMap.isEmpty()) {
            sb.append(getString(R.string.key_characteristic_empty));
        } else {
            sb.append(getString(R.string.key_characteristic))
                    .append(" ");
            for (Map.Entry<UUID, Integer> pair : keyCharacteristicsIdMap.entrySet()) {
                Characteristic keyChar = getController().getCharacteristicByID(pair.getKey());
                if (keyChar == null) continue;
                String s = keyChar.getTitle();
                sb.append(s)
                        .append("(" + pair.getValue() + "%)")
                        .append(", ");
            }
            sb.delete(sb.length() - 2, sb.length() - 1);
        }
        relatedCharacteristicsTextView.setText(sb.toString());
    }

    @Override
    public boolean isDependableDataAvailable() {
        return true;
    }

    protected void finish(String title, String message) {
        TreeMap <Characteristic, Integer> charsMap = new TreeMap<>();
        for (Map.Entry<UUID, Integer> pair : keyCharacteristicsIdMap.entrySet()) {
            charsMap.put(getController().getCharacteristicByID(pair.getKey()), pair.getValue());
        }
        getController().addSkill(title, charsMap);
        Toast.makeText(getCurrentActivity(), message, Toast.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateRelatedCharacteristicsView();
    }
}
