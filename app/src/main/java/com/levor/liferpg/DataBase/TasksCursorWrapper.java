package com.levor.liferpg.DataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpg.DataBase.DataBaseSchema.TasksTable;
import com.levor.liferpg.Model.LifeEntity;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TasksCursorWrapper extends CursorWrapper {
    private LifeEntity lifeEntity;

    public TasksCursorWrapper(Cursor cursor, LifeEntity lifeEntity) {
        super(cursor);
        this.lifeEntity = lifeEntity;
    }

    public Task getTask() {
        String uuid = getString(getColumnIndex(TasksTable.Cols.UUID));
        String title = getString(getColumnIndex(TasksTable.Cols.TITLE));
        String relatedSkills = getString(getColumnIndex(TasksTable.Cols.RELATED_SKILLS));
        int repeatability = getInt(getColumnIndex(TasksTable.Cols.REPEATABILITY));
        List<Skill> skills = new ArrayList<>();
        String[] skillsArray = relatedSkills.split("::");
        for (String s : skillsArray) {
            if (s.equals("")) continue;
            skills.add(lifeEntity.getSkillByID(UUID.fromString(s)));
        }
        return new Task(title, UUID.fromString(uuid), repeatability, skills);
    }

}
