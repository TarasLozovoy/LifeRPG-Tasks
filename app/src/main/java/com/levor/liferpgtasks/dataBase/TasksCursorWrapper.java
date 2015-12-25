package com.levor.liferpgtasks.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpgtasks.dataBase.DataBaseSchema.TasksTable;
import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;

import java.util.ArrayList;
import java.util.Date;
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
        int difficulty = getInt(getColumnIndex(TasksTable.Cols.DIFFICULTY));
        int importance = getInt(getColumnIndex(TasksTable.Cols.IMPORTANCE));
        long dateLong = getLong(getColumnIndex(TasksTable.Cols.DATE));
        int notifyInt = getInt(getColumnIndex(TasksTable.Cols.NOTIFY));
        boolean notify = notifyInt == 1;
        List<Skill> skills = new ArrayList<>();
        String[] skillsArray = relatedSkills.split("::");
        for (String s : skillsArray) {
            if (s.equals("")) continue;
            skills.add(lifeEntity.getSkillByID(UUID.fromString(s)));
        }

        Date date  = new Date(dateLong);
        return new Task(title, UUID.fromString(uuid), repeatability, difficulty, importance, date, notify, skills);
    }

}
