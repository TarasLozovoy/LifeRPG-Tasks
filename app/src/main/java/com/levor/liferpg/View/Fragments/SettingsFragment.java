package com.levor.liferpg.View.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.levor.liferpg.R;

public class SettingsFragment extends DefaultFragment {
    private TextView dropboxSyncValueTV;
    private Button enableDropboxButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        dropboxSyncValueTV = (TextView) v.findViewById(R.id.dropbox_activation_status_value);
        enableDropboxButton = (Button) v.findViewById(R.id.enable_dropbox_button);

        updateDropboxUI();
        enableDropboxButton.setOnClickListener(new EnableDropboxClickListener());
        return v;
    }

    private void updateDropboxUI() {
        dropboxSyncValueTV.setText(getCurrentActivity().isSaveToDropbox() ? R.string.enabled : R.string.disabled);
        dropboxSyncValueTV.setTextColor(getResources().getColor(getCurrentActivity().isSaveToDropbox() ?
                android.R.color.holo_green_light : android.R.color.holo_red_dark));
        enableDropboxButton.setText(getCurrentActivity().isSaveToDropbox() ? R.string.disable : R.string.enable);
    }

    private class EnableDropboxClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (getCurrentActivity().isSaveToDropbox()) {
                getCurrentActivity().setSaveToDropbox(false);
                getCurrentActivity().stopDropboxSync();
            } else {
                getCurrentActivity().startDropboxAuthorisation();
            }
            updateDropboxUI();
        }
    }
}
