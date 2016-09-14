package com.levor.liferpgtasks.view.fragments.hero;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.TextUtils;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

public class HeroFragment extends DefaultFragment {
    TextView heroNameTextView, heroLevelTextView, xpProgressTextView, moneyTextView;
    ImageView heroImageImageView;
    ProgressBar xpProgress;
    CircularProgressView circularProgressView;

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
        circularProgressView = (CircularProgressView) v.findViewById(R.id.progress_view);

        loadHeroBitmap(heroImageImageView);
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

    public void loadHeroBitmap(ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute();
    }

    class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Void... params) {
            return getCurrentActivity().getHeroIconBitmap();
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setVisibility(View.VISIBLE);
                    circularProgressView.setVisibility(View.GONE);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
