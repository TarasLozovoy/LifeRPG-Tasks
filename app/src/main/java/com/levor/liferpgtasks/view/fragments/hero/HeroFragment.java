package com.levor.liferpgtasks.view.fragments.hero;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.TextUtils;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.text.DecimalFormat;

public class HeroFragment extends DefaultFragment {
    TextView heroNameTextView, heroLevelTextView, xpProgressTextView, moneyTextView;
    ImageView heroImageImageView;
    ProgressBar xpProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hero_main, container, false);
        heroNameTextView = (TextView) v.findViewById(R.id.hero_name);
        heroImageImageView = (ImageView) v.findViewById(R.id.hero_image);
        xpProgress = (ProgressBar) v.findViewById(R.id.xp_progressbar);
        xpProgressTextView = (TextView) v.findViewById(R.id.xp_progress_TV);
        heroLevelTextView = (TextView) v.findViewById(R.id.hero_level);
        moneyTextView = (TextView) v.findViewById(R.id.money);

        heroImageImageView.setImageBitmap(getCurrentActivity().getHeroIconBitmap());
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

    public void updateUI() {
        xpProgress.setMax((int) getController().getHeroXpToNextLevel());
        xpProgress.setProgress((int) getController().getHeroXp());
        DecimalFormat df = TextUtils.DECIMAL_FORMAT;
        String xpString = getString(R.string.XP) + " : " + df.format(getController().getHeroXp()) +
                "/" + df.format(getController().getHeroXpToNextLevel());
        xpProgressTextView.setText(xpString);
        heroNameTextView.setText(getController().getHeroName());
        String heroLvl = getString(R.string.hero_level)+ " " + getController().getHeroLevel();
        heroLevelTextView.setText(heroLvl);

        moneyTextView.setText(df.format(getController().getHero().getMoney()));
    }
}
