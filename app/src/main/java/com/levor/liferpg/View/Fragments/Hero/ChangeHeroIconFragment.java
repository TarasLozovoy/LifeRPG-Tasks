package com.levor.liferpg.View.Fragments.Hero;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;

import java.io.IOException;
import java.io.InputStream;

public class ChangeHeroIconFragment extends DefaultFragment{
    private AssetManager assets;
    private String[] imageNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_hero_icon, container, false);
        assets = getContext().getAssets();
        RecyclerView recyclerView = (RecyclerView) v;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        try {
            imageNames = assets.list("");
        } catch (IOException e) {
            Log.e("Assets", "Could not list assets", e);
        }

        recyclerView.setAdapter(new ImageAdapter(imageNames));

        setHasOptionsMenu(true);
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ImageHolder extends RecyclerView.ViewHolder {
        private ImageButton image;

        public ImageHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.list_item_change_hero_image, container, false));
            image = (ImageButton) itemView.findViewById(R.id.change_hero_image_item);
        }

        public void bindImage(final String name){
            try {
                InputStream is = assets.open(name);
                Drawable d = Drawable.createFromStream(is, null);
                image.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCurrentActivity().setHeroImageName(name);
                    getCurrentActivity().showNthPreviousFragment(2);
                }
            });
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {
        private String[] names;

        public ImageAdapter(String[] names){
            this.names = names;
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ImageHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {
            String name = names[position];
            holder.bindImage(name);
        }

        @Override
        public int getItemCount() {
            return names.length;
        }
    }
}
