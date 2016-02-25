package com.levor.liferpgtasks.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpgtasks.Utils.TimeUnitUtils;
import com.levor.liferpgtasks.dataBase.DataBaseSchema.TasksTable;
import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.model.Task;

import org.joda.time.LocalDate;

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
        long notifyLong = getLong(getColumnIndex(TasksTable.Cols.NOTIFY));
        int dateMode = getInt(getColumnIndex(TasksTable.Cols.DATE_MODE));
        int repeatMode = getInt(getColumnIndex(TasksTable.Cols.REPEAT_MODE));
        int repeatIndex = getInt(getColumnIndex(TasksTable.Cols.REPEAT_INDEX));
        int habitDays = getInt(getColumnIndex(TasksTable.Cols.HABIT_DAYS));
        int habitDaysLeft = getInt(getColumnIndex(TasksTable.Cols.HABIT_DAYS_LEFT));
        Long habitStartDateMillis = getLong(getColumnIndex(TasksTable.Cols.HABIT_START_DATE));
        String repeatDaysOfWeekString = getString(getColumnIndex(TasksTable.Cols.REPEAT_DAYS_OF_WEEK));
        List<Skill> skills = new ArrayList<>();
        String[] skillsArray = relatedSkills.split("::");
        for (String s : skillsArray) {
            if (s.equals("")) continue;
            skills.add(lifeEntity.getSkillByID(UUID.fromString(s)));
        }

        Date date  = new Date(dateLong);
        LocalDate habitStartDate = LocalDate.fromDateFields(new Date(habitStartDateMillis));

        Task task = new Task(title, UUID.fromString(uuid));
        task.setDate(date);
        task.setDateMode(dateMode);
        task.setRepeatability(repeatability);
        task.setRepeatMode(repeatMode);
        task.setRepeatDaysOfWeekFromString(repeatDaysOfWeekString);
        task.setRepeatIndex(repeatIndex == 0 ? 1 : repeatIndex);
        task.setDifficulty(difficulty);
        task.setImportance(importance);
        task.setNotifyDelta(notifyLong);
        task.setRelatedSkills(skills);
        task.setHabitDays(habitDays);
        task.setHabitDaysLeft(habitDaysLeft);
        task.setHabitStartDate(habitStartDate);
        return task;
    }

}
