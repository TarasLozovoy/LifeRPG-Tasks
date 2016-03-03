package com.levor.liferpgtasks.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import org.joda.time.LocalDate;

import java.util.Map;

public class TasksPerDayCursorWrapper extends CursorWrapper {

    public TasksPerDayCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public void getTasksPerDay(Map<LocalDate, Integer> values) {
        long dateMillis = getLong(getColumnIndex(DataBaseSchema.TasksPerDayTable.Cols.DATE));
        int tasks = getInt(getColumnIndex(DataBaseSchema.TasksPerDayTable.Cols.TASKS_PERFORMED));

        LocalDate localDate = new LocalDate(dateMillis);
        values.put(localDate, tasks);
    }
}
