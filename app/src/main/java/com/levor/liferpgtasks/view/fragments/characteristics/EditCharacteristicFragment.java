package com.levor.liferpgtasks.view.fragments.characteristics;

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
import android.widget.Toast;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;


public class EditCharacteristicFragment extends DefaultFragment {
    public static String CHARACTERISTIC_TAG = "characteristic_tag";
    private Characteristic currentCharacteristic;

    @Bind(R.id.new_characteristic_title_edit_text) EditText titleEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_characteristic, container, false);
        ButterKnife.bind(this, v);
        if (getArguments() != null) {
            currentCharacteristic = getController().getCharacteristicByID((UUID) getArguments().get(CHARACTERISTIC_TAG));
            titleEditText.setText(currentCharacteristic.getTitle());
        }

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(currentCharacteristic != null ?
                currentCharacteristic.getTitle()
                : getString(R.string.add_new_characteristic));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Edit characteristic Fragment");
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
        inflater.inflate(R.menu.menu_edit_characteristic, menu);

        if (currentCharacteristic == null) {
            MenuItem item = menu.findItem(R.id.remove_menu_item);
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ok_menu_item:
                if (titleEditText.getText().toString().equals("")){
                    Toast.makeText(getContext(), getString(R.string.empty_characteristic_title_error), Toast.LENGTH_SHORT).show();
                } else if (getController().getSkillByTitle(titleEditText.getText().toString()) != null){
                    Toast.makeText(getContext(), getString(R.string.duplicate_characteristic_title_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (currentCharacteristic == null) {
                        currentCharacteristic = new Characteristic(titleEditText.getText().toString(), 1);
                        getController().addCharacteristic(currentCharacteristic);
                    } else {
                        currentCharacteristic.setTitle(titleEditText.getText().toString());
                        getController().updateCharacteristic(currentCharacteristic);
                    }
                    getCurrentActivity().showPreviousFragment();
                }
                return true;
            case R.id.remove_menu_item:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(currentCharacteristic.getTitle())
                        .setMessage(getString(R.string.removing_characteristic_message))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getController().removeCharacteristic(currentCharacteristic);
                                getCurrentActivity().showNthPreviousFragment(2);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
