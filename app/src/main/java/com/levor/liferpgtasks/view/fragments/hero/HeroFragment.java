package com.levor.liferpgtasks.view.fragments.hero;


import android.os.Bundle;
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

import java.text.DecimalFormat;

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
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Hero Fragment");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getCurrentActivity() == null && !isVisibleToUser) return;
        getCurrentActivity().showFab(true);
        getCurrentActivity().setFabImage(R.drawable.ic_mode_edit_black_24dp);
        getCurrentActivity().setFabClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new EditHeroFragment(), null);
            }
        });
    }

    @Override
    public boolean isFabVisible() {
        return true;
    }

    public void updateUI() {
        xpProgress.setMax((int) getController().getHeroXpToNextLevel());
        xpProgress.setProgress((int) getController().getHeroXp());
        DecimalFormat df = new DecimalFormat("#.##");
        String xpString = getString(R.string.XP) + " : " + df.format(getController().getHeroXp()) +
                "/" + df.format(getController().getHeroXpToNextLevel());
        xpProgressTV.setText(xpString);
        heroNameTV.setText(getController().getHeroName());
        String heroLvl = getString(R.string.hero_level)+ " " + getController().getHeroLevel();
        heroLevelTV.setText(heroLvl);
    }
}
