package com.levor.liferpg.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Activities.MainActivity;
import com.levor.liferpg.View.Fragments.HeroMainFragment;

import java.util.List;

/**
 * Created by Levor on 22.10.2015.
 */
public class TasksAdapter extends BaseAdapter implements ListAdapter{
    private Context mContext;
    private List<String> items;
    private MainActivity activity;
    private LifeController lifeController = LifeController.getInstance();

    public TasksAdapter(Context context, List<String> array, MainActivity activity) {
        this.mContext = context;
        this.items = array;
        this.activity = activity;
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tasks_list_view, null);
        }

        TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
        listItemText.setText(items.get(position));

        Button doneBtn = (Button) view.findViewById(R.id.check_button);
        final View finalView = view;
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Task task = lifeController.getTaskByTitle(items.get(position));
                boolean heroLevelIncreased = false;
                StringBuilder sb = new StringBuilder();
                sb.append("Task successfully performed!\n")
                        .append("Skill(s) improved:\n");
                for(Skill sk: task.getRelatedSkills()){
                    sb.append(sk.getTitle())
                            .append(": ")
                            .append(sk.getLevel())
                            .append("(")
                            .append(sk.getSublevel())
                            .append(")");
                    if (lifeController.changeSkillSubLevel(sk, true) && !heroLevelIncreased){
                        heroLevelIncreased = true;
                    }
                    sb.append(" -> ")
                            .append(sk.getLevel())
                            .append("(")
                            .append(sk.getSublevel())
                            .append(")")
                            .append("\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final boolean finalHeroLevelIncreased = heroLevelIncreased;
                builder.setTitle(items.get(position))
                        .setCancelable(false)
                        .setMessage(sb.toString())
                        .setPositiveButton("Nice!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                notifyDataSetChanged();
                                if (finalHeroLevelIncreased){
                                    Snackbar.make(finalView, "Congratulations!\n" + lifeController.getHeroName()
                                            + "'s level increased!", Snackbar.LENGTH_LONG)
                                            .setAction("Go to Hero page", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    activity.showRootFragment(new HeroMainFragment(), null);
                                                }
                                            })
                                            .show();
                                }
                                activity.saveAppData();
                            }
                        })
                        .setNegativeButton("Undo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Task undone.");
                                for(Skill sk: task.getRelatedSkills()){
                                    lifeController.changeSkillSubLevel(sk, false);
                                    sb.append("\n").append(sk.getTitle()).append(" skill returned to previous state");
                                }
                                Snackbar.make(finalView, sb.toString(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return view;
    }
}