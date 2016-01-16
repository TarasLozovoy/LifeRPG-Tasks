package com.levor.liferpgtasks.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.levor.liferpgtasks.R;

import java.util.List;

public class HighlightStringAdapter extends ArrayAdapter<String> {

    private int selectedIndex = -1;
    private Context context;

    public HighlightStringAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    public void setSelection(int position) {
        selectedIndex =  position;
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView =  super.getDropDownView(position, convertView, parent);

        if (position == selectedIndex) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.accent));
        } else {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        return itemView;
    }
}
