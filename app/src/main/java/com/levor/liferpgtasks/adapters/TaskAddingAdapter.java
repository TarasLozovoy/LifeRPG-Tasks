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

public class TaskAddingAdapter extends BaseAdapter implements ListAdapter {
    private Context context;
    protected List<String> items;


    public TaskAddingAdapter(Context context, List<String> array){
        this.context = context;
        this.items = array;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.task_add_list_view, null);
        }

        TextView listItemText = (TextView) view;
        listItemText.setText(items.get(position));
        return view;
    }
}
