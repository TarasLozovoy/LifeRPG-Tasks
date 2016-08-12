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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
        long finishDateLong = getLong(getColumnIndex(TasksTable.Cols.FINISH_DATE));
        long notifyLong = getLong(getColumnIndex(TasksTable.Cols.NOTIFY));
        int dateMode = getInt(getColumnIndex(TasksTable.Cols.DATE_MODE));
        int repeatMode = getInt(getColumnIndex(TasksTable.Cols.REPEAT_MODE));
        int repeatIndex = getInt(getColumnIndex(TasksTable.Cols.REPEAT_INDEX));
        int habitDays = getInt(getColumnIndex(TasksTable.Cols.HABIT_DAYS));
        int habitDaysLeft = getInt(getColumnIndex(TasksTable.Cols.HABIT_DAYS_LEFT));
        int numberOfExecutions = getInt(getColumnIndex(TasksTable.Cols.NUMBER_OF_EXECUTIONS));
        Long habitStartDateMillis = getLong(getColumnIndex(TasksTable.Cols.HABIT_START_DATE));
        String repeatDaysOfWeekString = getString(getColumnIndex(TasksTable.Cols.REPEAT_DAYS_OF_WEEK));
        Map<Skill, Boolean> skills = new TreeMap<>();
        String[] skillsArray = relatedSkills.split("::");
        for (String s : skillsArray) {
            String[] skillString = s.split(":;");
            String skillTitle = skillString[0];
            if (skillTitle.equals("")) continue;
            Skill skill = lifeEntity.getSkillByID(UUID.fromString(skillTitle));
            if (skill == null) continue;
            if (skillString.length == 1) {
                skills.put(skill, true);
            } else {
                skills.put(skill, skillString[1].equals("+"));
            }
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
        task.setNumberOfExecutions(numberOfExecutions);

        if (repeatability == 0 && task.getFinishDate() == null) {
            Date finishDate = finishDateLong > 0 ? new Date(finishDateLong) : new Date();
            task.setFinishDate(finishDate);
            task.setDateMode(Task.DateMode.SPECIFIC_TIME);
            task.setUpdateNeeded(true);
        } else if (repeatability < 0 && finishDateLong > 0) {
            task.setFinishDate(new Date(finishDateLong));
        }
        return task;
    }

}
