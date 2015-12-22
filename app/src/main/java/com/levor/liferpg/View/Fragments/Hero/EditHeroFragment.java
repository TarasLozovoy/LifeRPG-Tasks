package com.levor.liferpg.View.Fragments.Hero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;

public class EditHeroFragment extends DefaultFragment{
    private EditText editHeroName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_hero, container, false);
        editHeroName = (EditText) view.findViewById(R.id.edit_name_edit_hero_fragment);
        editHeroName.setText(getController().getHeroName());
        editHeroName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getCurrentActivity().showSoftKeyboard(false, getView());
                }
            }
        });
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Edit hero");
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
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
