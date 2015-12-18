package com.levor.liferpg.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Activities.MainActivity;
import com.levor.liferpg.View.Fragments.Hero.HeroFragment;

import java.util.List;

public class TasksAdapter extends BaseAdapter implements ListAdapter{
    private List<String> items;
    private MainActivity activity;
    private LifeController lifeController;

    public TasksAdapter(List<String> array, MainActivity activity) {
        this.items = array;
        this.activity = activity;
        lifeController = LifeController.getInstance(activity);
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

        Button doBtn = (Button) view.findViewById(R.id.check_button);
        final View finalView = view;
        doBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isHeroLevelIncreased = lifeController.performTask(task);
                if (isHeroLevelIncreased) {
                    Snackbar.make(finalView, "Congratulations!\n" + lifeController.getHeroName()
                            + "'s level increased!", Snackbar.LENGTH_LONG)
                            .setAction("Go to Hero page", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activity.showRootFragment(new HeroFragment(), null);
                                }
                            })
                            .show();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(items.get(position))
                        .setCancelable(false)
                        .setMessage(finalView.getResources().getString(R.string.task_performed))
                        .setNeutralButton(finalView.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                notifyDataSetChanged();
                            }
                        })
                        .setPositiveButton(finalView.getResources().getString(R.string.share), new ShareClickListener(items.get(position)));
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        TextView listItemTV = (TextView) view.findViewById(R.id.list_item_string);
        listItemTV.setText(task.getTitle());

        TextView repeatabilityTV = (TextView) view.findViewById(R.id.repeatability_tasks_list_item);
        LinearLayout repeatabilityLL = (LinearLayout) view.findViewById(R.id.repeatability_container_tasks_list_item);
        int repeat = task.getRepeatability();
        if (repeat < 0) {
            repeatabilityLL.setBackground(view.getResources().getDrawable(R.drawable.ic_sync_black_24dp));
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

    private class ShareClickListener implements DialogInterface.OnClickListener{
        private String taskTitle;

        public ShareClickListener(String task){
            this.taskTitle = task;
        }

        @Override
        public void onClick(DialogInterface dialog, int which){
            ShareDialog shareDialog = new ShareDialog(activity);
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(taskTitle + " " + activity.getResources().getString(R.string.done))
                        .setContentDescription(
                                "I have just finished task " + taskTitle + "!")
                        .setContentUrl(Uri.parse(activity.getResources().getString(R.string.facebook_app_link)))
                        .build();

                shareDialog.show(linkContent);
            }
        }
    }
}