package com.example.taskapp.adapters;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskapp.R;
import com.example.taskapp.dbHelpers.TaskDbHelper;
import com.example.taskapp.enums.NotificationTypeEnum;
import com.example.taskapp.feedEntries.TaskContract;
import com.example.taskapp.models.TaskModel;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private final RecyclerView recyclerView;
    private static List<TaskModel> tasksDataSet;
    private TaskModel recentlyDeletedItem;
    private int recentlyDeletedPosition;

    private TaskModel editedItem;
    private int editedItemPosition;

    private TaskDbHelper taskDbHelper;
    private SQLiteDatabase writableDatabase;



    public static class ViewHolder extends RecyclerView.ViewHolder{
        private static final int EDIT_ACTIVITY_REQUEST_CODE = 1;
        private final TextView task_Name;
        private final TextView task_Date;
        private final TextView task_Time;
        private final Button editButton;
        private TaskAdapter taskAdapter;

        private TextView input_editTaskName;
        private TextView input_editTaskTime;
        private CalendarView calendar_editTaskDate;
        private Calendar selectedDate;
        private Switch switch_editNotify;
        private RadioGroup radioGroup_editNotificationType;


        public ViewHolder(@NonNull View itemView, TaskAdapter adapter) {
            super(itemView);
            taskAdapter = adapter;
            task_Name = (TextView) itemView.findViewById(R.id.dashboard_item_taskName);
            task_Date = (TextView) itemView.findViewById(R.id.dashboard_item_taskDate);
            task_Time = (TextView) itemView.findViewById(R.id.dashboard_item_taskTime);
            editButton = itemView.findViewById(R.id.dashboard_item_editButton);
            editButton.setOnClickListener(v -> saveButtonOnClickListener(itemView));
        }

        private void saveButtonOnClickListener(View v) {
            taskAdapter.editedItem = tasksDataSet.get(getAdapterPosition());
            taskAdapter.editedItemPosition = getAdapterPosition();

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View editView = LayoutInflater.from(v.getContext()).inflate(R.layout.activity_edit, null);
            setupEditView(editView, builder);
            builder.setView(editView);
            builder.setTitle("Edytuj: " + taskAdapter.editedItem.getTaskName());
            builder.setCancelable(false);
            builder.setPositiveButton("Zapisz", (dialog, which) -> {
                dialog.dismiss();
                EditDialogSaveEvent(v);
            });
            builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.dismiss());
            builder.show();
        }

        private void setupEditView(View editView, AlertDialog.Builder builder) {
            input_editTaskName = editView.findViewById(R.id.input_editTaskName);
            input_editTaskTime  = editView.findViewById(R.id.input_editTaskTime);
            calendar_editTaskDate = editView.findViewById(R.id.calendar_editTaskDate);
            switch_editNotify = editView.findViewById(R.id.switch_editNotify);
            radioGroup_editNotificationType = editView.findViewById(R.id.radioGroup_editNotificationType);
            NotificationTypeEnum[] enumConstants = NotificationTypeEnum.class.getEnumConstants();
            for(int i = 0; i < enumConstants.length; i++) {
                RadioButton rb = new RadioButton(editView.getContext());
                rb.setEnabled(false);
                rb.setText(enumConstants[i].toString());
                rb.setId(i);
                radioGroup_editNotificationType.addView(rb);
            }


            setViewValuesFromEditedItem(editView);
            setViewListeners(editView);
        }

        private void setViewListeners(View editView) {
            input_editTaskTime.setOnClickListener(v -> editTimeListener(editView));
            calendar_editTaskDate.setOnDateChangeListener((view, year, month, dayOfMonth) -> editTaskDateListener(year, month, dayOfMonth));
            switch_editNotify.setOnCheckedChangeListener((buttonView, isChecked) -> editNotifyListener());
        }

        private void editNotifyListener() {
            setRadioButtonsEnabled(switch_editNotify.isChecked());
        }

        private void editTaskDateListener(int year, int month, int dayOfMonth) {
            selectedDate = new GregorianCalendar(year, month, dayOfMonth);
        }

        private void editTimeListener(View v) {
            int hour = 0;
            int minute = 0;

            TimePickerDialog mTimePicker = new TimePickerDialog(v.getContext(), (timePicker, selectedHour, selectedMinute) -> {
                String txtHour = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                String txtMinute = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                input_editTaskTime.setText(txtHour + ":" + txtMinute + ":00");
            }, hour, minute, true);

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }

        private void setViewValuesFromEditedItem(View v) {
            input_editTaskName.setText(taskAdapter.editedItem.getTaskName());
            input_editTaskTime.setText(taskAdapter.editedItem.getTaskTime());
            selectedDate = new GregorianCalendar();
            String date = taskAdapter.editedItem.getTaskDate();
            int year = Integer.parseInt(date.substring(0,4));
            int month = Integer.parseInt(date.substring(5,7));
            Toast.makeText(v.getContext(), ""+month, Toast.LENGTH_SHORT).show();
            int day = Integer.parseInt(date.substring(8,10));
            selectedDate.set(year, month-1, day);
            calendar_editTaskDate.setDate(selectedDate.getTimeInMillis());
            boolean notify = taskAdapter.editedItem.getNotify();
            switch_editNotify.setChecked(notify);
            setRadioButtonsEnabled(notify);
            if(notify) checkNotificationType(taskAdapter.editedItem, v);
        }

        private void checkNotificationType(TaskModel taskModel, View v) {
            NotificationTypeEnum notificationType = taskModel.getNotificationType();
            int id = notificationType.ordinal();
            radioGroup_editNotificationType.check(id);
        }

        private void setRadioButtonsEnabled(boolean enabled) {
            int count = radioGroup_editNotificationType.getChildCount();
            for (int i = 0; i < count; i++) {
                View o = radioGroup_editNotificationType.getChildAt(i);
                if(o instanceof RadioButton) {
                    o.setEnabled(enabled);
                }
            }
        }

        private void EditDialogSaveEvent(View v) {
            String taskName = input_editTaskName.getText().toString();
            String taskTime = input_editTaskTime.getText().toString();
            long datepicker = selectedDate.getTimeInMillis();
            Date datetime = new Date(datepicker);
            boolean notify = switch_editNotify.isChecked();
            int checkedRadioButtonId = 0;
            NotificationTypeEnum notificationType = NotificationTypeEnum.MIN30;
            if(notify) {
                checkedRadioButtonId = radioGroup_editNotificationType.getCheckedRadioButtonId();
                int length = NotificationTypeEnum.values().length;
                if(checkedRadioButtonId > length)
                    checkedRadioButtonId = 1;
                notificationType = NotificationTypeEnum.values()[checkedRadioButtonId];
            }

            taskAdapter.editedItem.setTaskName(taskName);
            taskAdapter.editedItem.setTaskDate(datetime.toString());
            taskAdapter.editedItem.setTaskTime(taskTime);
            taskAdapter.editedItem.setNotify(notify);
            taskAdapter.editedItem.setNotificationType(notificationType);

            taskAdapter.updateItem();
            Toast.makeText(v.getContext(), "TEST", Toast.LENGTH_SHORT).show();
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

    private void updateItem() {
        String taskName = editedItem.getTaskName();
        String date = editedItem.getTaskDate();
        String time = editedItem.getTaskTime();
        NotificationTypeEnum notificationType = editedItem.getNotificationType();
        boolean notify = editedItem.getNotify();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME, taskName);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE, date);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME, time);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFICATION_TYPE, notificationType.toString());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFY, notify);

        String whereClause = "_id = " + editedItem.getId();
        writableDatabase.update(TaskContract.TaskEntry.TABLE_NAME, values, whereClause, null);

        tasksDataSet.set(editedItemPosition, editedItem);
        notifyDataSetChanged();
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

        return new ViewHolder(view, this);
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
        Toast toast = Toast.makeText(recyclerView.getContext(), R.string.taskAdapter_removedToast, 1000);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private void undoDelete(RecyclerView recyclerView) {
        String taskName = recentlyDeletedItem.getTaskName();
        String date = recentlyDeletedItem.getTaskDate();
        String time = recentlyDeletedItem.getTaskTime();
        NotificationTypeEnum notificationType = recentlyDeletedItem.getNotificationType();
        boolean notify = recentlyDeletedItem.getNotify();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME, taskName);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE, date);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME, time);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFICATION_TYPE, notificationType.toString());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFY, notify);

        writableDatabase.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);

        tasksDataSet.add(recentlyDeletedPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedPosition);
        Toast toast = Toast.makeText(recyclerView.getContext(), R.string.taskAdapter_undoToast, 1000);
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
