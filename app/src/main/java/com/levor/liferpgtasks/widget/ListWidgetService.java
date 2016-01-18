package com.levor.liferpgtasks.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsFactory {
        private Context context;
        private int appWidgetId;

        private List<String> list;

        public ListRemoteViewsFactory(Context applicationContext, Intent intent) {
            context = applicationContext;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            LifeController lifeController = LifeController.getInstance(context.getApplicationContext());
            List<Task> tasks = lifeController.getAllTasks();
            Collections.sort(tasks, Task.DATE_ASC_TASKS_COMPARATOR);
            list = new ArrayList<>();
            for (Task t : tasks){
                list.add(t.getTitle());
            }
        }

        @Override
        public void onDataSetChanged() {
            LifeController lifeController = LifeController.getInstance(context.getApplicationContext());
            List<Task> tasks = lifeController.getAllTasks();
            Collections.sort(tasks, Task.DATE_ASC_TASKS_COMPARATOR);
            list = new ArrayList<>();
            for (Task t : tasks){
                list.add(t.getTitle());
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews row = new RemoteViews(context.getPackageName(),
                    R.layout.widget_list_item);
            row.setTextViewText(R.id.widget_list_item, list.get(position));

            String taskTitle = list.get(position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(LifeController.TASK_TITLE_NOTIFICATION_TAG, taskTitle);
            row.setOnClickFillInIntent(android.R.id.text1, fillInIntent);
            return row;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
