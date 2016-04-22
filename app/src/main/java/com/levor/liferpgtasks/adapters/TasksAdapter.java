package com.levor.liferpgtasks.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.PerformTaskAlertBuilder;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.levor.liferpgtasks.view.fragments.tasks.DetailedTaskFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>{
    private List<Task> items = new ArrayList<>();
    private MainActivity activity;
    private LifeController lifeController;
    private int position;

    public TasksAdapter(List<String> array, MainActivity activity) {
        this.activity = activity;
        lifeController = LifeController.getInstance(activity.getApplicationContext());
        for (int i = 0; i < array.size(); i++) {
            Task task = lifeController.getTaskByTitle(array.get(i));
            if (task != null) {
                items.add(task);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View tasksView = inflater.inflate(R.layout.tasks_list_item, parent, false);
        return new ViewHolder(tasksView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Task task = items.get(position);
        ImageButton doBtn = holder.doBtn;
        TextView titleTextView = holder.titleTextView;
        TextView dateTextView = holder.dateTextView;
        TextView repeatabilityTV = holder.repeatabilityTV;
        TextView habitDaysLeftTV = holder.habitDaysLeftTV;
        LinearLayout repeatabilityLL = holder.repeatabilityLL;
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID taskID = items.get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailedTaskFragment.SELECTED_TASK_UUID_TAG, taskID);
                activity.showChildFragment(new DetailedTaskFragment(), bundle);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });

        doBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformTaskAlertBuilder alert = new PerformTaskAlertBuilder(activity,
                        task);
                AlertDialog alertDialog = alert.create();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        notifyDataSetChanged();
                    }
                });
                alertDialog.show();
                notifyDataSetChanged();

            }
        });

        titleTextView.setText(task.getTitle());
        boolean isTaskFinished = task.getRepeatability() == 0;
        if (task.getDateMode() != Task.DateMode.TERMLESS || isTaskFinished) {
            Date date = isTaskFinished ? task.getFinishDate() : task.getDate();
            if (date != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(DateFormat.format(Task.getDateFormatting(), date));
                if (task.getDateMode() == Task.DateMode.SPECIFIC_TIME || isTaskFinished) {
                    sb.append(" ");
                    sb.append(DateFormat.format(Task.getTimeFormatting(), date));
                }
                dateTextView.setVisibility(View.VISIBLE);
                dateTextView.setText(sb.toString());
            }
        } else {
            dateTextView.setVisibility(View.GONE);
        }

        habitDaysLeftTV.setVisibility(View.GONE);
        int repeat = task.getRepeatability();
        if (repeat < 0) {
            if (task.getHabitDays() > 0) {
                habitDaysLeftTV.setVisibility(View.VISIBLE);
                habitDaysLeftTV.setText(String.valueOf(task.getHabitDaysLeft()));
            }
            int drawableId = task.getHabitDays() > 0 ? R.drawable.ic_generate_habit_black_24dp : R.drawable.infinity;
            repeatabilityLL.setBackground(activity.getResources().getDrawable(drawableId));
            repeatabilityTV.setText("");
            doBtn.setEnabled(true);
        } else if (repeat > 0) {
            repeatabilityLL.setBackground(activity.getResources().getDrawable(R.drawable.ic_replay_black_24dp));
            repeatabilityTV.setText(Integer.toString(repeat));
            doBtn.setEnabled(true);
        } else {
            repeatabilityLL.setBackground(null);
            repeatabilityTV.setText("");
            doBtn.setEnabled(false);
        }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView repeatabilityTV;
        TextView habitDaysLeftTV;
        LinearLayout repeatabilityLL;
        ImageButton doBtn;
        View root;

        public ViewHolder(View view) {
            super(view);
            root = view;
            doBtn = (ImageButton) view.findViewById(R.id.check_button);
            titleTextView = (TextView) view.findViewById(R.id.list_item_title);
            dateTextView = (TextView) view.findViewById(R.id.list_item_date);
            repeatabilityTV = (TextView) view.findViewById(R.id.repeatability_tasks_list_item);
            habitDaysLeftTV = (TextView) view.findViewById(R.id.habit_days_left_text_view);
            repeatabilityLL = (LinearLayout) view.findViewById(R.id.repeatability_container_tasks_list_item);
            itemView.setLongClickable(true);
        }
    }
}