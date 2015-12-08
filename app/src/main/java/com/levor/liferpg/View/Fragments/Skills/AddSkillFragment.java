package com.levor.liferpg.View.Fragments.Skills;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.levor.liferpg.Model.Characteristic;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSkillFragment extends DefaultFragment {
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
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(R.string.new_skill);
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add_skill, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_skill:
                if (titleEditText.getText().toString().equals("")){
                    Snackbar.make(getView(), "Skill title can't be empty", Snackbar.LENGTH_LONG).show();
                } else if (keyCharacteristic == null){
                    Snackbar.make(getView(), "Key characteristic should be set", Snackbar.LENGTH_LONG).show();
                } else if (getController().getSkillByTitle(titleEditText.getText().toString()) != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Oops!")
                            .setMessage("Skill with same title is already exists. Overwrite?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish(titleEditText.getText().toString(), "Skill added");
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
                    finish(titleEditText.getText().toString(), "Skill added");
                }
                return true;
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
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

    private class ChangeCharacteristicOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item
                    , getController().getCharacteristicsTitlesArray());
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Select key characteristic");
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
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }

    protected void setKeyCharacteristicByTitle(String title) {
        keyCharacteristic = getController().getCharacteristicByTitle(title);
        keyCharacteristicTV.setText(title);
    }
}
