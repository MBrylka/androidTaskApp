package com.example.taskapp.feedEntries;

import android.provider.BaseColumns;

public final class TaskContract {
    private TaskContract() {}

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TASK_NAME = "taskName";
        public static final String COLUMN_NAME_TASK_DATE = "taskDate";
        public static final String COLUMN_NAME_TASK_TIME = "taskTime";
    }
}
