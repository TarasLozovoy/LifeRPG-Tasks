package com.levor.liferpgtasks.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.levor.liferpgtasks.R;

public class ItemsWithImpactAlertBuilder extends AlertDialog.Builder {
    private AlertDialog alert;
    private Context context;
    private boolean showImpact;


    private MultiChoiceImpactListener listener;
    private String[] items;
    private Integer[] impacts;


    public ItemsWithImpactAlertBuilder(Context context, boolean showImpact) {
        super(context);
        this.context = context;
        this.showImpact = showImpact;
    }

    public ItemsWithImpactAlertBuilder setMultiChoiceItemsWithImpact(String[] items, Integer[] impacts, MultiChoiceImpactListener listener) {
        this.items = items;
        this.impacts = impacts;
        this.listener = listener;

        RecyclerView recyclerView = new RecyclerView(context);
        ImpactItemsRecyclerAdapter adapter = new ImpactItemsRecyclerAdapter(items, impacts, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        setView(recyclerView);
        return this;
    }

    public class ImpactItemsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private String[] items;
        private Integer[] impacts;
        private Context context;

        public ImpactItemsRecyclerAdapter(String[] array, Integer[] impacts, Context context) {
            this.context = context;
            this.items = array;
            this.impacts = impacts;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View tasksView = inflater.inflate(R.layout.impact_list_item, parent, false);
            return new ViewHolderItem(tasksView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final ViewHolderItem itemHolder = (ViewHolderItem) holder;

            //holder.bind should not trigger onCheckedChanged, it should just update UI
            itemHolder.checkBox.setOnCheckedChangeListener(null);

            itemHolder.bind(position);

            itemHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    itemHolder.impactTextView.setText(progress + "%");
                    impacts[holder.getAdapterPosition()] = progress;
                    listener.onChanged(items[holder.getAdapterPosition()], progress);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            itemHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (showImpact) {
                            itemHolder.impactLayout.setVisibility(View.VISIBLE);
                            itemHolder.seekBar.setProgress(100);
                        }
                        impacts[holder.getAdapterPosition()] = 100;
                        listener.onChanged(items[holder.getAdapterPosition()], 100);
                    } else {
                        itemHolder.impactLayout.setVisibility(View.GONE);
                        listener.onChanged(items[holder.getAdapterPosition()], -1);
                        impacts[holder.getAdapterPosition()] = -1;
                    }
                }
            });

            itemHolder.topLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemHolder.checkBox.setChecked(!itemHolder.checkBox.isChecked());
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        public class ViewHolderItem extends RecyclerView.ViewHolder {
            View topLayout;
            View impactLayout;
            TextView titleTextView;
            CheckBox checkBox;
            SeekBar seekBar;
            TextView impactTextView;
            View root;

            public ViewHolderItem(View view) {
                super(view);
                root = view;

                topLayout = view.findViewById(R.id.top_layout);
                titleTextView = (TextView) view.findViewById(R.id.title);
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);

                impactLayout = view.findViewById(R.id.impact_layout);
                seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
                impactTextView = (TextView) view.findViewById(R.id.impact);

                seekBar.setMax(100);
            }

            public void bind(int position) {
                String title = items[position];
                int impact = impacts[position];
                titleTextView.setText(title);
                if (impact < 0) {
                    impactLayout.setVisibility(View.GONE);
                    checkBox.setChecked(false);
                } else {
                    if (showImpact) {
                        impactLayout.setVisibility(View.VISIBLE);
                        impactTextView.setText(String.valueOf(impact) + "%");
                        seekBar.setProgress(impact);
                    }
                    checkBox.setChecked(true);
                }
            }
        }
    }

    public interface MultiChoiceImpactListener {
        void onChanged(String item, int newImpact);
    }
}
