package com.levor.liferpgtasks.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.levor.liferpgtasks.R;

import java.util.List;

public class TwoStringsRowAdapter extends BaseAdapter implements ListAdapter {
    private List<String[]> valuesList;
    private Context context;

    public TwoStringsRowAdapter(Context context, List<String[]> list) {
        this.context = context;
        this.valuesList = list;
    }

    @Override
    public int getCount() {
        return valuesList.size();
    }

    @Override
    public Object getItem(int position) {
        return valuesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.simple_list_item_2, null);
        }
        TextView textView1 = (TextView) view.findViewById(R.id.item_1);
        TextView textView2 = (TextView) view.findViewById(R.id.item_2);
        textView1.setText(valuesList.get(position)[0]);
        textView2.setText(valuesList.get(position)[1]);
        return view;
    }
}
