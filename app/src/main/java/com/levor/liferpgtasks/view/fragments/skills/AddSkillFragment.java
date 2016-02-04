package com.levor.liferpgtasks.view.fragments.skills;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DataDependantFrament;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

public class AddSkillFragment extends DataDependantFrament {
    public static final String RECEIVED_CHARACTERISTIC_TITLE_TAG = "received_characteristic_title_tag";

    protected final String SKILL_TITLE_TAG = "skill_title_tag";
    protected final String KEY_CHARACTERISTIC_TITLE = "key_characteristic_title";

    protected EditText titleEditText;
    protected TextView keyCharacteristicTV;
    protected Button setKeyCharacteristicButton;

    protected Characteristic keyCharacteristic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_skill, container, false);
        titleEditText = (EditText) v.findViewById(R.id.new_skill_title_edit_text);
        keyCharacteristicTV = (TextView) v.findViewById(R.id.key_characteristic_value);
        setKeyCharacteristicButton = (Button) v.findViewById(R.id.set_key_characteristic_button);
        setKeyCharacteristicButton.setOnClickListener(new ChangeCharacteristicOnClickListener());
        String title;
        if (getArguments()!= null && (title = getArguments().getString(RECEIVED_CHARACTERISTIC_TITLE_TAG)) != null){
            setKeyCharacteristicByTitle(title);
        }
        if (savedInstanceState != null) {
            titleEditText.setText(savedInstanceState.getString(SKILL_TITLE_TAG));
            String charTitle = savedInstanceState.getString(KEY_CHARACTERISTIC_TITLE);
            if (charTitle != null) {
                setKeyCharacteristicByTitle(charTitle);

            }
        }
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
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
                } else if (keyCharacteristic == null){
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
        if (keyCharacteristic != null) {
            outState.putSerializable(KEY_CHARACTERISTIC_TITLE, keyCharacteristic.getTitle());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean isDependableDataAvailable() {
        return true;
    }

    private class ChangeCharacteristicOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item
                    , getController().getCharacteristicsTitlesArray());
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(getString(R.string.select_characteristic));
            dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setKeyCharacteristicByTitle(adapter.getItem(which));
                    setKeyCharacteristicButton.setText(R.string.change_characteristic);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    protected void finish(String title, String message) {
        getController().addSkill(title, keyCharacteristic);
        Toast.makeText(getCurrentActivity(), message, Toast.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();

        getController().getGATracker().send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.GA_action))
                .setAction(getString(R.string.GA_add_new_skill))
                .build());
    }

    protected void setKeyCharacteristicByTitle(String title) {
        keyCharacteristic = getController().getCharacteristicByTitle(title);
        keyCharacteristicTV.setText(title);
    }
}
