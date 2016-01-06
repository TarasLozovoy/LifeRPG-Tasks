package com.levor.liferpgtasks.view.fragments.skills;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.R;

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
        titleEditText.setText(currentSkill.getTitle());
        setKeyCharacteristicByTitle(currentSkill.getKeyCharacteristic().getTitle());
        setKeyCharacteristicButton.setText(R.string.change_characteristic);
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Edit skill");
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_skill, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ok_menu_item:
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
                                    finish(titleEditText.getText().toString(), "Skill updated");
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
                    finish(titleEditText.getText().toString(), "Skill updated");
                }
                return true;
            case R.id.remove:
                removeSkill();
                return true;
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Edit Skill Fragment");
    }

    private void removeSkill(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Removing " + currentSkill.getTitle())
                .setMessage("Are you really want to remove this skill?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getController().removeSkill(currentSkill);
                        getCurrentActivity().showNthPreviousFragment(2);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void finish(String title, String message) {
        getCurrentActivity().showSoftKeyboard(false, getView());
        currentSkill.setTitle(title);
        currentSkill.setKeyCharacteristic(keyCharacteristic);
        getController().updateSkill(currentSkill);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }
}
