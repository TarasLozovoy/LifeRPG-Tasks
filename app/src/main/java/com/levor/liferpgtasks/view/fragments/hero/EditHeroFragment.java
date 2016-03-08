package com.levor.liferpgtasks.view.fragments.hero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

public class EditHeroFragment extends DefaultFragment{
    private EditText editHeroName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_hero, container, false);
        editHeroName = (EditText) view.findViewById(R.id.edit_name_edit_hero_fragment);
        Button changeIconButton = (Button) view.findViewById(R.id.change_hero_icon_button);

        editHeroName.setText(getController().getHeroName());
        editHeroName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getCurrentActivity().showSoftKeyboard(false, getView());
                }
            }
        });
        changeIconButton.setOnClickListener(new ChangeIconClickListener());

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.edit_hero_fragment_title));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_hero, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ok_menu_item:
                getController().updateHeroName(editHeroName.getText().toString());
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Edit Hero Fragment");
    }

    private class ChangeIconClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            getController().updateHeroName(editHeroName.getText().toString());
            getCurrentActivity().showChildFragment(new ChangeHeroIconFragment(), null);
        }
    }
}
