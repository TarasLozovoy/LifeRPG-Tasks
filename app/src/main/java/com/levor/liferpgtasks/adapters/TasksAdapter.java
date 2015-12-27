package com.levor.liferpgtasks.adapters;

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
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Task;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKShareDialog;
import com.vk.sdk.dialogs.VKShareDialogBuilder;

import java.net.MalformedURLException;
import java.net.URL;
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

        Button doBtn = (Button) view.findViewById(R.id.check_button);
        final View finalView = view;
        doBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isHeroLevelIncreased = lifeController.performTask(task, true);
                if (task.getRepeatability() == -1 || task.getRepeatability() > 0){
                    task.increaseDateByOneDay();
                    lifeController.updateTaskNotification(task);
                }
                if (isHeroLevelIncreased) {
                    Snackbar.make(finalView, "Congratulations!\n" + lifeController.getHeroName()
                            + "'s level increased!", Snackbar.LENGTH_LONG)
                            .setAction("Go to Hero page", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activity.switchToRootFragment(MainActivity.MAIN_FRAGMENT_ID);
                                }
                            })
                            .show();
                }
                notifyDataSetChanged();
                double xp = lifeController.getHero().getBaseXP() * task.getMultiplier();

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(items.get(position))
                        .setCancelable(false)
                        .setMessage(finalView.getResources().getString(R.string.task_performed) + "\n" + finalView.getResources().getString(R.string.XP_gained, xp))
                        .setNeutralButton(finalView.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(finalView.getResources().getString(R.string.share), null);
                final AlertDialog alert = builder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new ShareClickListener(items.get(position), alert));
                    }
                });
                alert.show();
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

    private class ShareClickListener implements View.OnClickListener{
        private String taskTitle;
        private AlertDialog dialog;

        public ShareClickListener(String task, AlertDialog dialog){
            this.taskTitle = task;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v){
            if (!lifeController.isInternetConnectionActive()) {
                Toast.makeText(activity, activity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                return;
            }
            boolean heroLevelIncreased = lifeController.performTask(lifeController.getTaskByTitle(taskTitle), false);
            if (heroLevelIncreased){
                Toast.makeText(activity, "Congratulations!\n" + lifeController.getHeroName()
                        + "'s level increased!", Toast.LENGTH_LONG)
                        .show();
            }
            notifyDataSetChanged();

            //facebook share dialog
//            ShareDialog shareDialog = new ShareDialog(activity);
//            if (ShareDialog.canShow(ShareLinkContent.class)) {
//                ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                        .setContentTitle(taskTitle + " " + activity.getResources().getString(R.string.done))
//                        .setContentDescription(
//                                "I have just finished task " + taskTitle + "!")
//                        .setContentUrl(Uri.parse(activity.getString(R.string.facebook_app_link)))
//                        .build();
//
//                shareDialog.show(linkContent);
//            }

            //vk share dialog
//            if (!VKSdk.isLoggedIn()){
//                lifeController.performVKLogin(activity);
//                Toast.makeText(activity, activity.getString(R.string.please_login), Toast.LENGTH_SHORT)
//                        .show();
//                return;
//            }
//            VKShareDialogBuilder vkShareDialog = new VKShareDialogBuilder();
//            vkShareDialog.setText(taskTitle + " " + activity.getResources().getString(R.string.done) +
//                            "\n" + "I have just finished task " + taskTitle + "!")
//                    .setAttachmentLink(activity.getString(R.string.app_name),
//                            activity.getString(R.string.facebook_app_link))
//                    .setShareDialogListener(new VKShareDialogBuilder.VKShareDialogListener() {
//                        @Override
//                        public void onVkShareComplete(int postId) {
//                            //TODO move here additional XP gaining
//                        }
//
//                        @Override
//                        public void onVkShareCancel() {
//
//                        }
//
//                        @Override
//                        public void onVkShareError(VKError error) {
//
//                        }
//                    })
//                    .show(activity.getSupportFragmentManager(), "VKShareDialog");

            //twitter share dialog
            try {
                new TweetComposer.Builder(activity)
                        .text(taskTitle + " " + activity.getResources().getString(R.string.done) +
                            "\n" + "I have just finished task " + taskTitle + "!")
                        .url(new URL(activity.getString(R.string.facebook_app_link)))
                        .show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            dialog.dismiss();
        }
    }
}