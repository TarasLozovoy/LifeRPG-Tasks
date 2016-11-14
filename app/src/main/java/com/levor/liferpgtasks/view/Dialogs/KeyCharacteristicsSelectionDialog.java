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
import com.levor.liferpgtasks.view.ItemsWithImpactAlertBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KeyCharacteristicsSelectionDialog extends DialogFragment {
    public static final String CHARS_MAP = "chars_list";
    public static final String WITH_IMPACT = "with_impact";

    private boolean showImpact;
    private TreeMap<String, Integer> characteristicsMap;
    private Context context;
    private KeyCharacteristicsChangedListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characteristicsMap = (TreeMap<String, Integer>) getArguments().get(CHARS_MAP);
            showImpact = getArguments().getBoolean(WITH_IMPACT, false);
            context = getContext();
        }
    }

    public void setListener(KeyCharacteristicsChangedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LifeController lifeController = LifeController.getInstance(context);
        List<Characteristic> allCharacteristics = lifeController.getCharacteristics();
        Collections.sort(allCharacteristics, Characteristic.LEVEL_COMPARATOR);
        String[] characteristicsNames = new String[allCharacteristics.size()];
        Integer[] characteristicsImpacts = new Integer[allCharacteristics.size()];

        for (int i = 0; i < allCharacteristics.size(); i++) {
            String characteristicTitle = allCharacteristics.get(i).getTitle();
            characteristicsNames[i] = characteristicTitle;
            Integer impact = characteristicsMap.get(characteristicTitle);
            characteristicsImpacts[i] = impact == null ? -1 : impact;
        }

        ItemsWithImpactAlertBuilder builder = new ItemsWithImpactAlertBuilder(context, showImpact);
        builder.setTitle(R.string.select_key_characteristic);
        builder.setMultiChoiceItemsWithImpact(characteristicsNames, characteristicsImpacts
                , new ItemsWithImpactAlertBuilder.MultiChoiceImpactListener() {
                    @Override
                    public void onChanged(String item, int newImpact) {
                        if (newImpact < 0) {
                            characteristicsMap.remove(item);
                        } else {
                            characteristicsMap.put(item, newImpact);
                        }
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onChanged(characteristicsMap);
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        return builder.create();
    }

    public interface KeyCharacteristicsChangedListener {
        void onChanged(TreeMap<String, Integer> characteristicsMap);
    }
}
