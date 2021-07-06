package com.example.taskapp.ui.dashboard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Layout;
import android.view.DragEvent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskapp.R;
import com.example.taskapp.adapters.TaskAdapter;
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
    private RecyclerView dashboard_recyclerView;

    private TaskDbHelper taskDbHelper;
    private SQLiteDatabase writableDatabase;

    private List<TaskModel> tasks = new ArrayList<TaskModel>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initializeComponents();
        initDatabase();

        initializeRecyclerView();

        loadAllTasks();

        TaskAdapter taskAdapter = new TaskAdapter(tasks);
        dashboard_recyclerView.setAdapter(taskAdapter);
        dashboard_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    private void initializeRecyclerView() {

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

        while (cursor.moveToNext()) {
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME));
            String taskDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE));
            String taskTime = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME));
            tasks.add(new TaskModel(taskName, taskDate, taskTime));
        }
        cursor.close();
    }
}