package com.levor.liferpgtasks.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<String> items = new ArrayList<>();
    private MainActivity activity;
    private int position;
    private View header;

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            View tasksView = inflater.inflate(R.layout.simple_list_item_1, parent, false);
            return new ViewHolderItem(tasksView);
        } else if (viewType == TYPE_HEADER) {
            if (header == null) {
                header = new View(activity); //dummy view for recyclerViews without header
            }
            return new ViewHolderHeader(header);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItem) {
            final ViewHolderItem itemHolder = (ViewHolderItem) holder;
            itemHolder.bind(items.get(position - 1));

            itemHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(position - 1);
                    }
                }
            });

            itemHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition(itemHolder.getPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position - 1;
    }

    public void registerOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }

    public void setHeader(View header) {
        this.header = header;
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView titleTextView;
        View root;

        public ViewHolderItem(View view) {
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

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public ViewHolderHeader(View itemView) {
            super(itemView);
        }
    }
}
