package com.levor.liferpgtasks.view.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressLint("ValidFragment")
public class SkillSelectionDialog extends DialogFragment {
    public static final String INCREASE_SKILLS_TAG = "increase_skills_tag";
    public static final String ACTIVE_LIST_TAG = "active_list_tag";
    public static final String NONACTIVE_LIST_TAG = "nonactive_list_tag";
    public static final String WITH_NEW_SKILL_BUTTON_TAG = "with_new_skill_tag";

    private LifeController lifeController;
    private MainActivity activity;

    private SkillSelectionListener listener;

    public static SkillSelectionDialog getInstance(MainActivity activity) {
        SkillSelectionDialog dialog = new SkillSelectionDialog();
        dialog.activity = activity;
        dialog.lifeController = LifeController.getInstance(activity);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final boolean increasingSkills = getArguments().getBoolean(INCREASE_SKILLS_TAG);
        final ArrayList<String> skillsList = getArguments().getStringArrayList(ACTIVE_LIST_TAG);
        ArrayList<String> nonActiveList = getArguments().getStringArrayList(NONACTIVE_LIST_TAG);
        if (nonActiveList == null) {
            nonActiveList = new ArrayList<>();
        }
        boolean withNewSkill = getArguments().getBoolean(WITH_NEW_SKILL_BUTTON_TAG, true);
        List<Skill> allSkills = lifeController.getAllSkills();
        allSkills.removeAll(Collections.singleton(null));
        Collections.sort(allSkills, Skill.TITLE_COMPARATOR);
        List<String> skillNames = new ArrayList<>();
        List<Boolean> skillStates = new ArrayList<>();

        for (int i = 0; i < allSkills.size(); i++) {
            String skillName = allSkills.get(i).getTitle();
            if (!nonActiveList.contains(skillName)) {
                skillNames.add(allSkills.get(i).getTitle());
                skillStates.add(skillsList.contains(allSkills.get(i).getTitle()));
            }
        }

        final String[] items = new String[skillNames.size()];
        final boolean[] states = new boolean[skillStates.size()];
        for (int i = 0; i < skillStates.size(); i++) {
            items[i] = skillNames.get(i);
            states[i] = skillStates.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.skill_choosing)
                .setMultiChoiceItems(items, states,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                                if (isChecked) {
                                    skillsList.add(items[item]);
                                } else {
                                    skillsList.remove(items[item]);
                                }
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onClose(increasingSkills, skillsList);
                        }
                    }
                })
                .setCancelable(false);
        if (withNewSkill) {
            builder.setNeutralButton(R.string.add_new_skill, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NewSkillDialog newSkillDialog = new NewSkillDialog();
                    Bundle b = new Bundle();
                    b.putBoolean(INCREASE_SKILLS_TAG, increasingSkills);
                    newSkillDialog.setArguments(b);
                    newSkillDialog.show(activity.getSupportFragmentManager(), "New skill");
                }
            });
        }

        return builder.create();
    }

    public void setListener(SkillSelectionListener listener) {
        this.listener = listener;
    }


    @SuppressLint("ValidFragment")
    public class NewSkillDialog extends DialogFragment {
        private EditText titleEditText;
        private View addCharView;
        private TextView addedCharsTextView;

        private TreeMap<String, Integer> charsMap = new TreeMap<>();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View dialogView = View.inflate(getContext(), R.layout.fragment_add_skill, null);
            titleEditText = (EditText) dialogView.findViewById(R.id.new_skill_title_edit_text);
            addCharView = dialogView.findViewById(R.id.add_characteristic_ll);
            addedCharsTextView = (TextView) dialogView.findViewById(R.id.related_characteristics_text_view);

            addCharView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyCharacteristicsSelectionDialog dialog = new KeyCharacteristicsSelectionDialog();
                    Bundle b = new Bundle();
                    b.putSerializable(KeyCharacteristicsSelectionDialog.CHARS_MAP, charsMap);
                    b.putBoolean(KeyCharacteristicsSelectionDialog.WITH_IMPACT, true);
                    dialog.setArguments(b);
                    dialog.setListener(new KeyCharacteristicsSelectionDialog.KeyCharacteristicsChangedListener() {
                        @Override
                        public void onChanged(TreeMap<String, Integer> characteristicsMap) {
                            charsMap = characteristicsMap;
                            updateCharacteristicsView();
                        }
                    });
                    dialog.show(activity.getSupportFragmentManager(), "CharacteristicsSelection")
                    ;
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final AlertDialog alert = builder.setView(dialogView)
                    .setPositiveButton(R.string.ok, null)
                    .create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (titleEditText.getText().toString().equals("")) {
                                Toast.makeText(getContext(), getString(R.string.empty_skill_title_error), Toast.LENGTH_SHORT).show();
                            } else if (charsMap.isEmpty()) {
                                Toast.makeText(getContext(), getString(R.string.no_key_characteristic_error), Toast.LENGTH_SHORT).show();
                            } else if (lifeController.getSkillByTitle(titleEditText.getText().toString()) != null) {
                                Toast.makeText(getContext(), getString(R.string.skill_duplicate_error_no_question), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), getString(R.string.skill_added_message), Toast.LENGTH_SHORT).show();
                                TreeMap<Characteristic, Integer> chars = new TreeMap<>();
                                for (Map.Entry<String, Integer> pair : charsMap.entrySet()) {
                                    chars.put(lifeController.getCharacteristicByTitle(pair.getKey()), pair.getValue());
                                }
                                String skillTitle = titleEditText.getText().toString();
                                lifeController.addSkill(skillTitle, chars);
                                dialog.dismiss();
                            if (listener != null) {
                                listener.onNewSkillAdded(skillTitle);
                            }
                            }
                        }
                    });
                }
            });
            return alert;
        }

        private void updateCharacteristicsView() {
            StringBuilder sb = new StringBuilder();
            if (charsMap.isEmpty()) {
                sb.append(getString(R.string.key_characteristic_empty));
            } else {
                sb.append(getString(R.string.key_characteristic))
                        .append(" ");
                for (Map.Entry<String, Integer> pair : charsMap.entrySet()) {
                    sb.append(pair.getKey())
                            .append("(" + pair.getValue() + "%)")
                            .append(", ");
                }
                sb.delete(sb.length() - 2, sb.length() - 1);
            }
            addedCharsTextView.setText(sb.toString());
        }
    }

    public interface SkillSelectionListener{
        void onNewSkillAdded(String skillTitle);
        void onClose(boolean increasingSkills, ArrayList<String> titles);
    }
}
