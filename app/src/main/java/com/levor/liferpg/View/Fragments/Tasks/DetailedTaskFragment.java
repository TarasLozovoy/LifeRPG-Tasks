package com.levor.liferpg.View.Fragments.Tasks;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;
import com.levor.liferpg.R;
import com.levor.liferpg.View.Fragments.DefaultFragment;
import com.levor.liferpg.View.Fragments.Skills.DetailedSkillFragment;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailedTaskFragment extends DefaultFragment {
    public final static String SELECTED_TASK_UUID_TAG = "selected_task_uuid_tag";

    private TextView taskTitle;
    private ListView listView;
    private Button removeTask;
    private Button performTask;
    private Button editTask;
    private Task currentTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detailed_task, container, false);

        taskTitle = (TextView) v.findViewById(R.id.task_title);
        listView = (ListView) v.findViewById(R.id.list_view);
        removeTask = (Button) v.findViewById(R.id.remove_task);
        performTask = (Button) v.findViewById(R.id.perform_task);
        editTask = (Button) v.findViewById(R.id.edit_task);

        UUID id = (UUID)getArguments().get(SELECTED_TASK_UUID_TAG);
        currentTask = getController().getTaskByID(id);
        taskTitle.setText(currentTask.getTitle());
        getCurrentActivity().setTitle(currentTask.getTitle() + " task details");


        createAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG, currentTask.getRelatedSkills().get(position).getId());
                Fragment f = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable(EditTaskFragment.CURRENT_TASK_TAG, currentTask.getTitle());
                Fragment f = new EditTaskFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        removeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Removing " + currentTask.getTitle())
                        .setMessage("Are you really want to remove this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getController().removeTask(currentTask);
                                getCurrentActivity().showPreviousFragment();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        performTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    sk.increaseSublevel();
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
                                    sk.decreaseSublevel();
                                    sb.append("\n").append(sk.getTitle()).append(" skill returned to previous state");
                                }
                                Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return v;
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
}
