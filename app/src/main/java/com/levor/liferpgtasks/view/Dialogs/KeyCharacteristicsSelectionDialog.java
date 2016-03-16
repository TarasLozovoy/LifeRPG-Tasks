package com.levor.liferpgtasks.view.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Characteristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyCharacteristicsSelectionDialog extends DialogFragment {
    public static final String CHARS_LIST = "chars_list";
//    public static final String CONTEXT = "context";
    private ArrayList<String> keyCharacteristicsTitleList;
    private Context context;
    private KeyCharacteristicsChangedListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyCharacteristicsTitleList = getArguments().getStringArrayList(CHARS_LIST);
            context = getContext();
        }
    }

    public void setListener(KeyCharacteristicsChangedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LifeController lifeController = LifeController.getInstance(context);
        List<Characteristic> allCharacteristics = lifeController.getCharacteristics();
        Collections.sort(allCharacteristics, Characteristic.LEVEL_COMPARATOR);
        String[] characteristicsNames = new String[allCharacteristics.size()];
        boolean[] characteristicsStates = new boolean[allCharacteristics.size()];

        for (int i = 0; i < allCharacteristics.size(); i++) {
            characteristicsNames[i] = allCharacteristics.get(i).getTitle();
            characteristicsStates[i] = keyCharacteristicsTitleList.contains(allCharacteristics.get(i).getTitle());
        }

        final String[] items = characteristicsNames;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.skill_choosing)
                .setMultiChoiceItems(items, characteristicsStates,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                                if (isChecked){
                                    keyCharacteristicsTitleList.add(items[item]);
                                } else {
                                    keyCharacteristicsTitleList.remove(items[item]);
                                }
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onChanged(keyCharacteristicsTitleList);
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        return builder.create();
    }

    public interface KeyCharacteristicsChangedListener {
        void onChanged(ArrayList<String> titles);
    }
}
