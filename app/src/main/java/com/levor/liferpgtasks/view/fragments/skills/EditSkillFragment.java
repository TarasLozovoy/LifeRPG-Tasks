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
                    Toast.makeText(getContext(), getString(R.string.empty_skill_title_error), Toast.LENGTH_SHORT).show();
                } else if (keyCharacteristic == null){
                    Toast.makeText(getContext(), getString(R.string.no_key_characteristic_error), Toast.LENGTH_SHORT).show();
                } else if (getController().getSkillByTitle(titleEditText.getText().toString()) != null
                        && !getController().getSkillByTitle(titleEditText.getText().toString()).equals(currentSkill)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.oops))
                            .setMessage(getString(R.string.skill_duplicate_error))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish(titleEditText.getText().toString(), getString(R.string.skill_updated_message));
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
                    finish(titleEditText.getText().toString(), getString(R.string.skill_updated_message));
                }
                return true;
            case R.id.remove:
                removeSkill();
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
        alert.setTitle(getString(R.string.skill_updated_message) + " " + currentSkill.getTitle())
                .setMessage(getString(R.string.removing_skill_message))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getController().removeSkill(currentSkill);
                        getCurrentActivity().showNthPreviousFragment(2);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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
