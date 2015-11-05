package com.levor.liferpg.View.Fragments.Skills;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.R;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditSkillFragment extends AddSkillFragment {
    public final static String EDIT_SKILL_UUID_TAG = "edit_skill_uuid_tag";

    private Skill currentSkill;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        currentSkill = getController().getSkillByID((UUID) getArguments().get(EDIT_SKILL_UUID_TAG));
        finishButton.setText(R.string.finish_editing);
        finishButton.setOnClickListener(new FinishButtonOnClickListener());
        titleEditText.setText(currentSkill.getTitle());
        keyCharacteristic = currentSkill.getKeyCharacteristic();
        keyCharacteristicTV.setText(keyCharacteristic.getTitle());
        setKeyCharacteristicButton.setText(R.string.change_characteristic);
        return v;
    }

    @Override
    protected void finishAddingSkill(String title, String message) {
        currentSkill.setTitle(title);
        currentSkill.setKeyCharacteristic(keyCharacteristic);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }

    private class FinishButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (titleEditText.getText().toString().equals("")){
                Toast.makeText(getActivity(), "Skill title can't be empty", Toast.LENGTH_SHORT).show();
            } else if (keyCharacteristic == null){
                Toast.makeText(getActivity(), "Key characteristic should be set", Toast.LENGTH_SHORT).show();
            } else if (getController().getSkillByTitle(titleEditText.getText().toString()) != null
                    && !getController().getSkillByTitle(titleEditText.getText().toString()).equals(currentSkill)){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Oops!")
                        .setMessage("Another skill with same title is already exists. Overwrite?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAddingSkill(titleEditText.getText().toString(), "Skill updated");
                            }
                        })
                        .setNegativeButton("No, change new skill title", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                finishAddingSkill(titleEditText.getText().toString(), "Skill updated");
            }
        }
    }
}
