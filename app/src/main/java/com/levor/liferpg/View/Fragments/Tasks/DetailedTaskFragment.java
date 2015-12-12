package com.levor.liferpg.View.Fragments.Tasks;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.levor.liferpg.View.Fragments.Hero.HeroFragment;
import com.levor.liferpg.View.Fragments.Skills.DetailedSkillFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class DetailedTaskFragment extends DefaultFragment {
    public final static String SELECTED_TASK_UUID_TAG = "selected_task_uuid_tag";

    private ListView listView;
    private Task currentTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detailed_task, container, false);

        TextView taskTitleTV = (TextView) v.findViewById(R.id.task_title);
        TextView taskDifficultyTV = (TextView) v.findViewById(R.id.task_difficulty_text_view);
        TextView taskImportanceTV = (TextView) v.findViewById(R.id.task_importance_text_view);
        TextView taskRepeatTV = (TextView) v.findViewById(R.id.task_repeat_times_text_view);
        listView = (ListView) v.findViewById(R.id.list_view);

        UUID id = (UUID)getArguments().get(SELECTED_TASK_UUID_TAG);
        currentTask = getController().getTaskByID(id);
        taskTitleTV.setText(currentTask.getTitle());

        //setup difficulty TextView
        int difficulty = currentTask.getDifficulty();
        String difficultyString = Arrays.asList(getResources()
                .getStringArray(R.array.difficulties_array)).get(difficulty);
        taskDifficultyTV.setText(getResources().getString(R.string.difficulty) +
                " " + difficultyString);

        //setup importance TextView
        String importanceString = Arrays.asList(getResources()
                .getStringArray(R.array.importance_array)).get(currentTask.getImportance());
        taskImportanceTV.setText(getResources().getString(R.string.importance) +
                " " + importanceString);

        //setup repeatability TextView
        int repeat = currentTask.getRepeatability();
        if (repeat == 0) {
            taskRepeatTV.setText(getResources().getString(R.string.task_finished));
        } else if (repeat < 0) {
            taskRepeatTV.setText(getResources().getString(R.string.infinite_number_of));
        } else {
            taskRepeatTV.setText(getResources().getString(R.string.repeat) + " " +
                    repeat + " " +
                    getResources().getString(R.string.times));
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.perform_task).setEnabled(!currentTask.isTaskDone());
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
        sb.append("Are you sure you want perform this task?\n");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(currentTask.getTitle())
                .setCancelable(false)
                .setMessage(sb.toString())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isHeroLevelIncreased = getController().performTask(currentTask);
                        dialog.dismiss();
                        createAdapter();
                        if (isHeroLevelIncreased) {
                            Snackbar.make(getView(), "Congratulations!\n" + getController().getHeroName()
                                    + "'s level increased!", Snackbar.LENGTH_LONG)
                                    .setAction("Go to Hero page", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getCurrentActivity().showRootFragment(new HeroFragment(), null);
                                        }
                                    })
                                    .show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
