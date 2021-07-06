package com.example.taskapp.ui.dashboard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.taskapp.R;
import com.example.taskapp.dbHelpers.TaskDbHelper;
import com.example.taskapp.feedEntries.TaskContract;
import com.example.taskapp.models.TaskModel;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class DashboardFragment extends Fragment {

    private View root;
    private DashboardViewModel dashboardViewModel;
    private LinearLayout dashboard_linearLayout;

    private TaskDbHelper taskDbHelper;
    private SQLiteDatabase writableDatabase;

    private List<TaskModel> tasks = new ArrayList<TaskModel>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initializeComponents();

        taskDbHelper = new TaskDbHelper(getContext());
        writableDatabase = taskDbHelper.getWritableDatabase();

        loadAllTasks();
        createTaskUI();
        return root;
    }


    private void initializeComponents() {
        dashboard_linearLayout = root.findViewById(R.id.dashboard_linearLayout);
    }

    private void loadAllTasks() {
        String[] projection = {
                BaseColumns._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE,
                TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME
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

        while(cursor.moveToNext()) {
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME));
            String taskDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE));
            String taskTime = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME));
            tasks.add(new TaskModel(taskName, taskDate, taskTime));
        }
        cursor.close();
    }

    private void createTaskUI() {
        for(TaskModel task: tasks) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dashboardRecord = inflater.inflate(R.layout.dashboard_record, null);
            TextView textView1 = dashboardRecord.findViewById(R.id.dashboard_item_taskName);
            TextView textView2 = dashboardRecord.findViewById(R.id.dashboard_item_taskDate);
            TextView textView3 = dashboardRecord.findViewById(R.id.dashboard_item_taskTime);

            textView1.setText(task.taskName);
            textView2.setText(task.taskDate);
            textView3.setText(task.taskTime);

            dashboard_linearLayout.addView(dashboardRecord);
        }
    }

}