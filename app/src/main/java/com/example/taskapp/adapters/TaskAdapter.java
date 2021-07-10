package com.example.taskapp.adapters;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskapp.R;
import com.example.taskapp.dbHelpers.TaskDbHelper;
import com.example.taskapp.feedEntries.TaskContract;
import com.example.taskapp.models.TaskModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private final RecyclerView recyclerView;
    private List<TaskModel> tasksDataSet;
    private TaskModel recentlyDeletedItem;
    private int recentlyDeletedPosition;

    private TaskDbHelper taskDbHelper;
    private SQLiteDatabase writableDatabase;



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView task_Name;
        private final TextView task_Date;
        private final TextView task_Time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            task_Name = (TextView) itemView.findViewById(R.id.dashboard_item_taskName);
            task_Date = (TextView) itemView.findViewById(R.id.dashboard_item_taskDate);
            task_Time = (TextView) itemView.findViewById(R.id.dashboard_item_taskTime);
        }

        public TextView getTask_NameView() {
            return task_Name;
        }

        public TextView getTask_DateView() {
            return task_Date;
        }

        public TextView getTask_TimeView() {
            return task_Time;
        }
    }

    public TaskAdapter(List<TaskModel> dataSet, RecyclerView rv) {
        tasksDataSet = dataSet;
        recyclerView = rv;
        initDatabase();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_record, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTask_NameView().setText(tasksDataSet.get(position).getTaskName());
        holder.getTask_DateView().setText(tasksDataSet.get(position).getTaskDate());
        holder.getTask_TimeView().setText(tasksDataSet.get(position).getTaskTime());
    }

    @Override
    public int getItemCount() {
        return tasksDataSet.size();
    }

    public void deleteItem(int position) {
        recentlyDeletedItem = tasksDataSet.get(position);
        recentlyDeletedPosition = position;
        tasksDataSet.remove(position);
        notifyItemRemoved(position);
        removeItemFromDatabase();
        showUndoSnackbar(recentlyDeletedItem);
    }

    private void showUndoSnackbar(TaskModel recentlyDeletedItem) {

        Snackbar snackbar = Snackbar.make(recyclerView, R.string.dashboard_snackbar, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.dashboard_snackbar_undo, v -> undoDelete(recyclerView));
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if(event != DISMISS_EVENT_ACTION) {
                    super.onDismissed(transientBottomBar, event);
                }
            }
        });
        snackbar.show();
    }

    private void removeItemFromDatabase() {
        String tableName = TaskContract.TaskEntry.TABLE_NAME;
        String whereClause = TaskContract.TaskEntry._ID + "=" + recentlyDeletedItem.getId();

        writableDatabase.delete(tableName, whereClause, null);

        Log.d("remove from db", recentlyDeletedItem.toString());
        Toast toast = Toast.makeText(recyclerView.getContext(), "Usunięto zadanie.", 1000);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private void undoDelete(RecyclerView recyclerView) {
        String taskName = recentlyDeletedItem.getTaskName();
        String date = recentlyDeletedItem.getTaskDate();
        String time = recentlyDeletedItem.getTaskTime();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME, taskName);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE, date);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME, time);

        writableDatabase.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);


        tasksDataSet.add(recentlyDeletedPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedPosition);
        Toast toast = Toast.makeText(recyclerView.getContext(), "Przywrócono zadanie.", 1000);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private void initDatabase() {
        taskDbHelper = new TaskDbHelper(recyclerView.getContext());
        writableDatabase = taskDbHelper.getWritableDatabase();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
