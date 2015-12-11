package com.levor.liferpg.View.Fragments.Tasks;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;
import com.levor.liferpg.View.Fragments.Skills.DetailedSkillFragment;

import java.util.ArrayList;
import java.util.UUID;

public class DetailedTaskFragment extends DefaultFragment {
    public final static String SELECTED_TASK_UUID_TAG = "selected_task_uuid_tag";

    private TextView taskTitleTV;
    private TextView taskRepeatTV;
    private ListView listView;
    private Task currentTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detailed_task, container, false);

        taskTitleTV = (TextView) v.findViewById(R.id.task_title);
        taskRepeatTV = (TextView) v.findViewById(R.id.task_repeat_times_text_view);
        listView = (ListView) v.findViewById(R.id.list_view);

        UUID id = (UUID)getArguments().get(SELECTED_TASK_UUID_TAG);
        currentTask = getController().getTaskByID(id);
        taskTitleTV.setText(currentTask.getTitle());
        int repeat = currentTask.getRepeatability();
        if (repeat == 0) {
            taskRepeatTV.setText(v.getResources().getString(R.string.task_finished));
        } else if (repeat < 0) {
            taskRepeatTV.setText(v.getResources().getString(R.string.infinite_number_of));
        } else {
            taskRepeatTV.setText(v.getResources().getString(R.string.repeat) + " " +
                            repeat + " " +
                            v.getResources().getString(R.string.times));
        }
        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle("Task");
        getCurrentActivity().showActionBarHomeButtonAsBack(true);

        createAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG, currentTask.getRelatedSkills().get(position).getId());
                DefaultFragment f = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_detailed_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_task:
                Bundle b = new Bundle();
                b.putSerializable(EditTaskFragment.CURRENT_TASK_TAG, currentTask.getTitle());
                DefaultFragment f = new EditTaskFragment();
                getCurrentActivity().showChildFragment(f, b);
                return true;
            case R.id.perform_task:
                performTask();
                return true;
            case android.R.id.home:
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createAdapter(){
        ArrayList<String> skills = new ArrayList<>();
        for (Skill sk : currentTask.getRelatedSkills()) {
            StringBuilder sb = new StringBuilder(sk.getTitle());
            sb.append(" - ")
                    .append(sk.getLevel())
                    .append("(")
                    .append(sk.getSublevel())
                    .append(")");
            skills.add(sb.toString());
        }
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, skills.toArray()));
    }

    private void performTask(){
        StringBuilder sb = new StringBuilder();
        sb.append("Task successfully performed!\n")
                .append("Skill(s) improved:\n");
        for(Skill sk: currentTask.getRelatedSkills()){
            sb.append(sk.getTitle())
                    .append(": ")
                    .append(sk.getLevel())
                    .append("(")
                    .append(sk.getSublevel())
                    .append(")");
            getCurrentActivity().getController().changeSkillSubLevel(sk, true);
            sb.append(" -> ")
                    .append(sk.getLevel())
                    .append("(")
                    .append(sk.getSublevel())
                    .append(")")
                    .append("\n");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(currentTask.getTitle())
                .setCancelable(false)
                .setMessage(sb.toString())
                .setPositiveButton("Nice!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        createAdapter();
                    }
                })
                .setNegativeButton("Undo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Task undone.");
                        for(Skill sk: currentTask.getRelatedSkills()){
                            getCurrentActivity().getController().changeSkillSubLevel(sk, false);
                            sb.append("\n").append(sk.getTitle()).append(" skill returned to previous state");
                        }
                        Snackbar.make(getView(), sb.toString(), Snackbar.LENGTH_LONG).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
