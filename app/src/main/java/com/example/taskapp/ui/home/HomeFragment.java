package com.example.taskapp.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.taskapp.R;
import com.example.taskapp.dbHelpers.TaskDbHelper;
import com.example.taskapp.enums.NotificationTypeEnum;
import com.example.taskapp.feedEntries.TaskContract;
import com.example.taskapp.models.TaskModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    View root;

    private TaskDbHelper taskDbHelper;
    private SQLiteDatabase writableDatabase;
    private LinearLayout scrollView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        initializeComponents();
        initDatabase();
        loadData();

        return root;
    }

    private void loadData() {
        String[] projection = {
                BaseColumns._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFICATION_TYPE,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFY
        };

        String sortOrder = TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE + " DESC";
        // >= DATEADD(day, -7, GETDATE())
        String selection = TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE + " <= DATETIME('now', '+7 day')";
        Cursor cursor = writableDatabase.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME));
            String taskDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE));
            String taskTime = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME));
            String notificationType = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFICATION_TYPE));
            boolean notify = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFY));

            View record = View.inflate(root.getContext(), R.layout.home_record, null);
            TextView taskNameView = record.findViewById(R.id.home_item_taskName);
            TextView taskTimeView = record.findViewById(R.id.home_item_taskTime);
            TextView taskDateView = record.findViewById(R.id.home_item_taskDate);
            TextView taskNotifyView = record.findViewById(R.id.home_item_notify);

            taskNameView.setText(taskName);
            taskTimeView.setText(taskTime);
            taskDateView.setText(taskDate);

            if(!notify) {
                taskNotifyView.setText("Powiadomienie: brak");
            } else {
                taskNotifyView.setText("Powiadomienie: " + notificationType);
            }

            scrollView.addView(record);
        }
        cursor.close();
    }

    private void initializeComponents() {
        scrollView = root.findViewById(R.id.home_scrollViewLayout);
    }

    private void initDatabase() {
        taskDbHelper = new TaskDbHelper(getContext());
        writableDatabase = taskDbHelper.getWritableDatabase();
    }
}