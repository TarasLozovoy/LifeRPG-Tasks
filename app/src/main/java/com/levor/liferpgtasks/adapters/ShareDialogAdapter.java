package com.levor.liferpgtasks.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.view.activities.MainActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKShareDialogBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShareDialogAdapter extends BaseAdapter implements ListAdapter{
    private static final int FACEBOOK_ID = 0;
    private static final int TWITTER_ID = 1;
    private static final int G_PLUS_ID = 2;
    private static final int VK_ID = 3;

    private static final int DELAY_MILLIS = 3000;

    private LifeController lifeController;
    private Context context;
    private List<String> items = new ArrayList<>(4);
    private String taskTitle;

    public ShareDialogAdapter(Context context, String taskTitle){
        this.context = context;
        lifeController = LifeController.getInstance(context.getApplicationContext());
        this.taskTitle = taskTitle;
        items.add(FACEBOOK_ID, context.getString(R.string.facebook));
        items.add(TWITTER_ID, context.getString(R.string.twitter));
        items.add(G_PLUS_ID, context.getString(R.string.g_plus));
        items.add(VK_ID, context.getString(R.string.vk));
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.share_dialog_list_item, null);
        }

        TextView title = (TextView) view.findViewById(R.id.social_network_title);
        Button shareButton = (Button) view.findViewById(R.id.social_network_button);
        ImageView imageView = (ImageView) view.findViewById(R.id.social_image_logo);

        title.setText(items.get(position));
        double xp = lifeController.getHero().getBaseXP() * lifeController.getTaskByTitle(taskTitle).getShareMultiplier();
        shareButton.setText(context.getString(R.string.share_with_xp, xp));
        switch (position){
            case FACEBOOK_ID:
                shareButton.setOnClickListener(new FacebookShareClickListener());
                imageView.setImageResource(R.drawable.facebook_logo);
                break;
            case TWITTER_ID:
                shareButton.setOnClickListener(new TwitterShareClickListener());
                imageView.setImageResource(R.drawable.twitter_logo);
                break;
            case G_PLUS_ID:
                shareButton.setOnClickListener(new GPlusShareClickListener());
                imageView.setImageResource(R.drawable.g_plus_logo);
                break;
            case VK_ID:
                shareButton.setOnClickListener(new VKShareClickListener());
                imageView.setImageResource(R.drawable.vk_logo);
                break;
        }
        return view;
    }

    private boolean checkInternetConnection(){
        if (!lifeController.isInternetConnectionActive()) {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void gainAdditionalXP(String taskTitle){
        boolean heroLevelIncreased = lifeController.shareTask(lifeController.getTaskByTitle(taskTitle));
        if (heroLevelIncreased){
            Toast.makeText(context, "Congratulations!\n" + lifeController.getHeroName()
                    + "'s level increased!", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private class FacebookShareClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (!checkInternetConnection()) return;
            ShareDialog shareDialog = new ShareDialog((Activity)context);
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(taskTitle + " " + context.getResources().getString(R.string.done))
                        .setContentDescription(
                                "I have just finished task " + taskTitle + "!")
                        .setContentUrl(Uri.parse(context.getString(R.string.facebook_app_link)))
                        .build();

                shareDialog.show(linkContent);
            }
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gainAdditionalXP(taskTitle);
                    double xp = lifeController.getHero().getBaseXP() *
                            lifeController.getTaskByTitle(taskTitle).getShareMultiplier();
                    Button currentButton = (Button) v;
                    currentButton.setText(context.getString(R.string.XP_gained, xp));
                    currentButton.setEnabled(false);
                }
            }, DELAY_MILLIS);
        }
    }

    private class TwitterShareClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (!checkInternetConnection()) return;
            try {
                new TweetComposer.Builder(context)
                        .text(taskTitle + " " + context.getString(R.string.done) +
                                "\n" + "I have just finished task " + taskTitle + "!")
                        .url(new URL(context.getString(R.string.facebook_app_link)))
                        .show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gainAdditionalXP(taskTitle);
                    double xp = lifeController.getHero().getBaseXP() *
                            lifeController.getTaskByTitle(taskTitle).getShareMultiplier();
                    Button currentButton = (Button) v;
                    currentButton.setText(context.getString(R.string.XP_gained, xp));
                    currentButton.setEnabled(false);
                }
            }, DELAY_MILLIS);
        }
    }

    private class GPlusShareClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (!checkInternetConnection()) return;
            Intent shareIntent = new PlusShare.Builder(context)
                    .setType("text/plain")
                    .setText(taskTitle + " " + context.getString(R.string.done) +
                            "\n" + "I have just finished task " + taskTitle + "!")
                    .setContentUrl(Uri.parse(context.getString(R.string.facebook_app_link)))
                    .getIntent();

            context.startActivity(shareIntent);
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gainAdditionalXP(taskTitle);
                    double xp = lifeController.getHero().getBaseXP() *
                            lifeController.getTaskByTitle(taskTitle).getShareMultiplier();
                    Button currentButton = (Button) v;
                    currentButton.setText(context.getString(R.string.XP_gained, xp));
                    currentButton.setEnabled(false);
                }
            }, DELAY_MILLIS);
        }
    }

    private class VKShareClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (!checkInternetConnection()) return;
            if (!VKSdk.isLoggedIn()){
                lifeController.performVKLogin((Activity) context);
                Toast.makeText(context, context.getString(R.string.please_login), Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            VKShareDialogBuilder vkShareDialog = new VKShareDialogBuilder();
            vkShareDialog.setText(taskTitle + " " + context.getString(R.string.done) +
                    "\n" + "I have just finished task " + taskTitle + "!")
                    .setAttachmentLink(context.getString(R.string.app_name),
                            context.getString(R.string.facebook_app_link))
                    .setShareDialogListener(new VKShareDialogBuilder.VKShareDialogListener() {
                        @Override
                        public void onVkShareComplete(int postId) {
                            gainAdditionalXP(taskTitle);
                            double xp = lifeController.getHero().getBaseXP() *
                                    lifeController.getTaskByTitle(taskTitle).getShareMultiplier();
                            Button currentButton = (Button) v;
                            currentButton.setText(context.getString(R.string.XP_gained, xp));
                            currentButton.setEnabled(false);
                        }

                        @Override
                        public void onVkShareCancel() {

                        }

                        @Override
                        public void onVkShareError(VKError error) {

                        }
                    })
                    .show(((MainActivity) context).getSupportFragmentManager(), "VKShareDialog");
        }
    }
}
