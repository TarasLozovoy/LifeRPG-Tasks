package com.levor.liferpgtasks.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.levor.liferpgtasks.R;

import java.util.ArrayList;
import java.util.List;

public class WhatsNewAdapter extends BaseAdapter implements ListAdapter {
    private List<String> versions = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private Context context;

    public WhatsNewAdapter(Context context, List<String> versions, List<String> descriptions) {
        this.context = context;
        this.versions = versions;
        this.descriptions = descriptions;
    }

    @Override
    public int getCount() {
        return versions.size();
    }

    @Override
    public Object getItem(int position) {
        return versions.get(versions.size() - 1 - position);
    }

    @Override
    public long getItemId(int position) {
        return versions.size() - 1 - position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.whats_new_list_item, null);
        }

        TextView versionTextView = (TextView) view.findViewById(R.id.version_name);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.version_description);

        versionTextView.setTypeface(null, Typeface.BOLD);
        versionTextView.setText(versions.get(versions.size() - 1 - position));
        descriptionTextView.setText(descriptions.get(descriptions.size() - 1 - position));

        return view;
    }
}
