package com.levor.liferpgtasks.view.Dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ListView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.adapters.WhatsNewAdapter;
import com.levor.liferpgtasks.controller.LifeController;

import java.util.ArrayList;
import java.util.Arrays;

public class WhatsNewDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = View.inflate(getContext(), R.layout.fragment_whats_new_view, null);
        ListView listView = (ListView) dialogView.findViewById(R.id.whats_new_listview);

        String[] versions = getContext().getResources().getStringArray(R.array.versions_list);
        String[] descriptions = getContext().getResources().getStringArray(R.array.version_descriptions_list);
        WhatsNewAdapter adapter = new WhatsNewAdapter(getContext(),
                new ArrayList<>(Arrays.asList(versions)),
                new ArrayList<>(Arrays.asList(descriptions)));
        listView.setAdapter(adapter);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(dialogView)
                .setNegativeButton(R.string.ok, null)
                .setPositiveButton(R.string.support_project, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LifeController.getInstance(getContext()).showDonationFragment();
                    }
                })
                .setTitle(R.string.whats_new);
        return alert.create();
    }
}
