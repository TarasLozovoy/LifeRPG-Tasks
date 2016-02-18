package com.levor.liferpgtasks.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.PerformTaskAlertBuilder;
import com.levor.liferpgtasks.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends BaseAdapter implements ListAdapter{
    private List<Task> items = new ArrayList<>();
    private MainActivity activity;
    private LifeController lifeController;

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
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position).getTitle();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tasks_list_item, null);
        }

        final Task task = items.get(position);
        ImageButton doBtn = (ImageButton) view.findViewById(R.id.check_button);
        final View finalView = view;
        doBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformTaskAlertBuilder alert = new PerformTaskAlertBuilder(activity,
                        task,
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
                if (isHeroLevelIncreased) {
                    Toast.makeText(activity, activity.getString(R.string.hero_level_increased, lifeController.getHeroName()),
                            Toast.LENGTH_LONG).show();
                }
                notifyDataSetChanged();

            }
        });

        TextView listItemTitleTextView = (TextView) view.findViewById(R.id.list_item_title);
        TextView listItemDateTextView = (TextView) view.findViewById(R.id.list_item_date);

        listItemTitleTextView.setText(task.getTitle());
        if (task.getRepeatability() != 0 && task.getDateMode() != Task.DateMode.TERMLESS) {
            StringBuilder sb = new StringBuilder();
            sb.append(DateFormat.format(Task.getDateFormatting(), task.getDate()));
            if (task.getDateMode() == Task.DateMode.SPECIFIC_TIME) {
                sb.append(" ");
                sb.append(DateFormat.format(Task.getTimeFormatting(), task.getDate()));
            }
            listItemDateTextView.setVisibility(View.VISIBLE);
            listItemDateTextView.setText(sb.toString());
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
            repeatabilityLL.setBackground(null);
            repeatabilityTV.setText("");
            doBtn.setEnabled(false);
        }
        return view;
    }
}