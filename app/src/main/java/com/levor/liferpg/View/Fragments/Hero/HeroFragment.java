package com.levor.liferpg.View.Fragments.Hero;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.Characteristics.CharacteristicsFragment;
import com.levor.liferpg.View.Fragments.DefaultFragment;
import com.levor.liferpg.View.Fragments.Skills.SkillsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeroFragment extends DefaultFragment {
    TextView heroNameTV, heroLevelTV, xpProgressTV;
    ImageView heroImageIV;
    Button openSkillsButton, openCharacteristicsButton;
    ProgressBar xpProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hero_main, container, false);
        heroNameTV = (TextView) v.findViewById(R.id.hero_name);
        heroImageIV = (ImageView) v.findViewById(R.id.hero_image);
        openSkillsButton = (Button) v.findViewById(R.id.open_skills_button);
        openCharacteristicsButton = (Button) v.findViewById(R.id.open_characteristics_button);
        xpProgress = (ProgressBar) v.findViewById(R.id.xp_progressbar);
        xpProgressTV = (TextView) v.findViewById(R.id.xp_progress_TV);
        heroLevelTV = (TextView) v.findViewById(R.id.hero_level);

        setButtonsOnClickListener();
        heroImageIV.setImageResource(R.drawable.default_hero);
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(R.id.hero);
        getCurrentActivity().showActionBarHomeButtonAsBack(false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        updateUI();
        super.onResume();
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

    private void updateUI() {
        xpProgress.setMax(getController().getHeroXpToNextLevel());
        xpProgress.setProgress(getController().getHeroXp());
        xpProgressTV.setText("XP : " + xpProgress.getProgress() + "/" + xpProgress.getMax());
        heroNameTV.setText(getController().getHeroName());
        heroLevelTV.setText("Level " + getController().getHeroLevel());
    }

    private void setButtonsOnClickListener() {
        openSkillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new SkillsFragment(), null);
            }
        });

        openCharacteristicsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showChildFragment(new CharacteristicsFragment(), null);
            }
        });
    }
}
