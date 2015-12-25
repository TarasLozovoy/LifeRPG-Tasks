package com.levor.liferpgtasks.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.levor.liferpgtasks.R;

public class SortingSpinner extends LinearLayout{
    private Spinner sortingSpinner;
    private ImageView sortImage;

    public SortingSpinner(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.sorting_spinner, this);
        sortingSpinner = (Spinner) findViewById(R.id.sorting_spinner);
        sortImage = (ImageView) findViewById(R.id.sort_image_view);

        sortImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortingSpinner.performClick();
            }
        });
    }

    public Spinner getSortingSpinner(){
        return sortingSpinner;
    }
}
