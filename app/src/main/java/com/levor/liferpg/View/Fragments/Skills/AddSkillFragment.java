package com.levor.liferpg.View.Fragments.Skills;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpg.Model.Characteristic;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSkillFragment extends DefaultFragment {
    protected EditText titleEditText;
    protected TextView keyCharacteristicTV;
    protected Button setKeyCharacteristicButton;
    protected Button finishButton;

    protected Characteristic keyCharacteristic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_skill, container, false);
        titleEditText = (EditText) v.findViewById(R.id.new_skill_title_edit_text);
        keyCharacteristicTV = (TextView) v.findViewById(R.id.key_characteristic_value);
        setKeyCharacteristicButton = (Button) v.findViewById(R.id.set_key_characteristic_button);
        setKeyCharacteristicButton.setOnClickListener(new ChangeCharacteristicOnClickListener());
        finishButton = (Button) v.findViewById(R.id.finish);
        finishButton.setOnClickListener(new FinishButtonOnClickListener());
        return v;
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
                    keyCharacteristic = getController().getCharacteristicByTitle(adapter.getItem(which));
                    keyCharacteristicTV.setText(keyCharacteristic.getTitle());
                    setKeyCharacteristicButton.setText(R.string.change_characteristic);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private class FinishButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (titleEditText.getText().toString().equals("")){
                Toast.makeText(getActivity(), "Skill title can't be empty", Toast.LENGTH_SHORT).show();
            } else if (keyCharacteristic == null){
                Toast.makeText(getActivity(), "Key characteristic should be set", Toast.LENGTH_SHORT).show();
            } else if (getController().getSkillByTitle(titleEditText.getText().toString()) != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Oops!")
                        .setMessage("Skill with same title is already exists. Overwrite?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAddingSkill(titleEditText.getText().toString(), "Skill added");
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
                finishAddingSkill(titleEditText.getText().toString(), "Skill added");
            }
        }
    }

    protected void finishAddingSkill(String title, String message) {
        getController().createNewSkill(title, keyCharacteristic);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        getCurrentActivity().showPreviousFragment();
    }
}
