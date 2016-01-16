package com.levor.liferpgtasks.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.PerformTaskAlertBuilder;
import com.levor.liferpgtasks.view.activities.MainActivity;

import java.util.Date;
import java.util.List;

public class TasksAdapter extends BaseAdapter implements ListAdapter{
    private List<String> items;
    private MainActivity activity;
    private LifeController lifeController;

    public TasksAdapter(List<String> array, MainActivity activity) {
        this.items = array;
        this.activity = activity;
        lifeController = LifeController.getInstance(activity.getApplicationContext());
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
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tasks_list_item, null);
        }

        final Task task = lifeController.getTaskByTitle(items.get(position));
        if (task == null) {
            return null;
        }
        Button doBtn = (Button) view.findViewById(R.id.check_button);
        final View finalView = view;
        doBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformTaskAlertBuilder alert = new PerformTaskAlertBuilder(activity,
                        lifeController.getTaskByTitle(items.get(position)),
                        finalView);
                AlertDialog alertDialog = alert.create();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        notifyDataSetChanged();
                    }
                });
                alertDialog.show();

                boolean isHeroLevelIncreased = lifeController.performTask(task);
                if (task.getRepeatability() == -1 || task.getRepeatability() > 0){
                    task.increaseDateByNDays(1);
                    lifeController.updateTaskNotification(task);
                }
                if (isHeroLevelIncreased) {
                    Toast.makeText(activity, "Congratulations!\n" + lifeController.getHeroName()
                            + "'s level increased!", Toast.LENGTH_SHORT)
                            .show();
                }
                notifyDataSetChanged();

                lifeController.getGATracker().send(new HitBuilders.EventBuilder()
                        .setCategory(activity.getString(R.string.GA_action))
                        .setAction(activity.getString(R.string.GA_task_performed))
                        .setValue(1)
                        .build());

                if (task.getRepeatability() == 0){
                    lifeController.getGATracker().send(new HitBuilders.EventBuilder()
                            .setCategory(activity.getString(R.string.GA_action))
                            .setAction(activity.getString(R.string.GA_task_finished))
                            .build());
                }

            }
        });

        TextView listItemTV = (TextView) view.findViewById(R.id.list_item_string);
        listItemTV.setText(task.getTitle());
        if (task.getDate().before(new Date(System.currentTimeMillis())) && task.getRepeatability() != 0){
            listItemTV.setTextColor(activity.getResources().getColor(R.color.red));
        } else {
            listItemTV.setTextColor(activity.getResources().getColor(R.color.gray));
        }
        TextView repeatabilityTV = (TextView) view.findViewById(R.id.repeatability_tasks_list_item);
        LinearLayout repeatabilityLL = (LinearLayout) view.findViewById(R.id.repeatability_container_tasks_list_item);
        int repeat = task.getRepeatability();
        if (repeat < 0) {
            repeatabilityLL.setBackground(view.getResources().getDrawable(R.drawable.infinity));
            repeatabilityTV.setText("");
            doBtn.setEnabled(true);
        } else if (repeat > 0) {
            repeatabilityLL.setBackground(view.getResources().getDrawable(R.drawable.ic_replay_black_24dp));
            repeatabilityTV.setText(Integer.toString(repeat));
            doBtn.setEnabled(true);
        } else {
            repeatabilityLL.setBackground(view.getResources().getDrawable(R.drawable.ic_done_black_24dp));
            repeatabilityTV.setText("");
            doBtn.setEnabled(false);
        }
        return view;
    }
}