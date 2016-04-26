package com.levor.liferpgtasks.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.characteristics.DetailedCharacteristicFragment;

import java.util.ArrayList;
import java.util.List;

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder>{
    private List<String> items = new ArrayList<>();
    private MainActivity activity;
    private LifeController lifeController;
    private int position;

    private OnRecycleItemClickListener listener;

    public SimpleRecyclerAdapter(List<String> array, MainActivity activity) {
        this.activity = activity;
        this.items = array;
    }

    public SimpleRecyclerAdapter(String[] array, MainActivity activity) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        this.activity = activity;
        this.items = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View tasksView = inflater.inflate(R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(tasksView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bind(items.get(position));

        lifeController = LifeController.getInstance(activity);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void registerOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        View root;

        public ViewHolder(View view) {
            super(view);
            root = view;
            titleTextView = (TextView) view.findViewById(R.id.item_2);
            itemView.setLongClickable(true);
        }

        public void bind(String text) {
            titleTextView.setText(text);
        }
    }

    public interface OnRecycleItemClickListener {
        void onItemClick(int position);
    }
}
