package com.example.taskapp.ui.dashboard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskapp.R;
import com.example.taskapp.adapters.TaskAdapter;
import com.example.taskapp.callbacks.TaskSwipeDeleteCallback;
import com.example.taskapp.dbHelpers.TaskDbHelper;
import com.example.taskapp.enums.NotificationTypeEnum;
import com.example.taskapp.feedEntries.TaskContract;
import com.example.taskapp.models.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private View root;
    private DashboardViewModel dashboardViewModel;
    private LinearLayout dashboard_linearLayout;
    private RecyclerView dashboard_recyclerView;

    private TaskDbHelper taskDbHelper;
    private SQLiteDatabase writableDatabase;
    private ItemTouchHelper taskItemTouchHelper;

    private List<TaskModel> tasks = new ArrayList<TaskModel>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initializeComponents();
        initDatabase();
        loadAllTasks();

        setupRecyclerView();

        return root;
    }

    private void setupRecyclerView() {
        TaskAdapter taskAdapter = new TaskAdapter(tasks, dashboard_recyclerView);
        dashboard_recyclerView.setAdapter(taskAdapter);
        dashboard_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskItemTouchHelper = new ItemTouchHelper(new TaskSwipeDeleteCallback(taskAdapter, dashboard_recyclerView));
        taskItemTouchHelper.attachToRecyclerView(dashboard_recyclerView);
    }

    private void initDatabase() {
        taskDbHelper = new TaskDbHelper(getContext());
        writableDatabase = taskDbHelper.getWritableDatabase();
    }


    private void initializeComponents() {
        dashboard_recyclerView = root.findViewById(R.id.dashboard_recyclerView);
    }

    private void loadAllTasks() {
        String[] projection = {
                BaseColumns._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFICATION_TYPE,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFY
        };

        String sortOrder = TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE + " DESC";
        Cursor cursor = writableDatabase.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                null,
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
            int notificationType = cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFICATION_TYPE));
            boolean notify = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFY));
            tasks.add(new TaskModel(id, taskName, taskDate, taskTime, NotificationTypeEnum.MIN30, notify));
        }
        cursor.close();
    }
}