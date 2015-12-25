package com.levor.liferpgtasks.view.fragments.hero;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeroFragment extends DefaultFragment {
    TextView heroNameTV, heroLevelTV, xpProgressTV;
    ImageView heroImageIV;
    ProgressBar xpProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hero_main, container, false);
        heroNameTV = (TextView) v.findViewById(R.id.hero_name);
        heroImageIV = (ImageView) v.findViewById(R.id.hero_image);
        xpProgress = (ProgressBar) v.findViewById(R.id.xp_progressbar);
        xpProgressTV = (TextView) v.findViewById(R.id.xp_progress_TV);
        heroLevelTV = (TextView) v.findViewById(R.id.hero_level);

        heroImageIV.setImageBitmap(getCurrentActivity().getHeroIconBitmap());
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_hero_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_hero:
                getCurrentActivity().showChildFragment(new EditHeroFragment(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void updateUI() {
        xpProgress.setMax((int) getController().getHeroXpToNextLevel());
        xpProgress.setProgress((int) getController().getHeroXp());
        String xpString = "XP : " + getController().getHeroXp() +
                "/" + getController().getHeroXpToNextLevel();
        xpProgressTV.setText(xpString);
        heroNameTV.setText(getController().getHeroName());
        heroLevelTV.setText("Level " + getController().getHeroLevel());
    }
}
