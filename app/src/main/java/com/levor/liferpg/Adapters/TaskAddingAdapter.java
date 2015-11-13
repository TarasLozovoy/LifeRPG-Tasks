package com.levor.liferpg.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.levor.liferpg.R;

import java.util.List;

/**
 * Created by Levor on 10/29/15.
 */
public class TaskAddingAdapter extends BaseAdapter implements ListAdapter {
    private Context mContext;
    protected List<String> items;


    public TaskAddingAdapter(Context context, List<String> array){
        this.mContext = context;
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.task_add_list_view, null);
        }

        TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
        listItemText.setText(items.get(position));
        return view;
    }
}
