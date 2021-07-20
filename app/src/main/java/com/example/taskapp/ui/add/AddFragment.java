package com.example.taskapp.ui.add;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.taskapp.R;
import com.example.taskapp.dbHelpers.TaskDbHelper;
import com.example.taskapp.enums.NotificationTypeEnum;
import com.example.taskapp.feedEntries.TaskContract;

import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;

public class AddFragment extends Fragment {

    private AddViewModel addTaskViewModel;
    private View root;

    private EditText input_taskName;
    private EditText input_taskTime;
    private CalendarView input_taskDate;
    private Switch switch_addNotify;
    private RadioGroup radioGroup_addNotificationType;
    private Button btn_add;

    private TaskDbHelper taskDbHelper;
    private SQLiteDatabase writableDatabase;

    private Calendar input_selectedDate;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addTaskViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
        root = inflater.inflate(R.layout.fragment_add, container, false);

        initializeComponents();
        addRadioButtons();
        setListeners();

        taskDbHelper = new TaskDbHelper(getContext());
        writableDatabase = taskDbHelper.getWritableDatabase();

        return root;
    }

    private void addRadioButtons() {
        for(NotificationTypeEnum type : NotificationTypeEnum.values()) {
            RadioButton rb = new RadioButton(root.getContext());
            rb.setEnabled(false);
            rb.setText(type.toString());
            radioGroup_addNotificationType.addView(rb);
        }
    }

    private void initializeComponents() {
        btn_add = root.findViewById(R.id.btn_add);
        input_taskName = root.findViewById(R.id.input_taskName);
        input_taskDate = root.findViewById(R.id.calendar_taskDate);
        input_taskTime = root.findViewById(R.id.input_taskTime);
        switch_addNotify = root.findViewById(R.id.switch_addNotify);
        radioGroup_addNotificationType = root.findViewById(R.id.radioGroup_addNotificationType);

    }

    private void setListeners() {
        btn_add.setOnClickListener(v -> btn_addOnClick());
        input_taskTime.setOnClickListener(v -> input_taskTimeOnClick());
        input_taskDate.setOnDateChangeListener((view, year, month, dayOfMonth) -> input_taskDateOnDateChangeListener(year, month, dayOfMonth));
        switch_addNotify.setOnCheckedChangeListener((buttonView, isChecked) -> switch_addNotifyOnCheckedChangeListener(buttonView, isChecked));
    }

    private void switch_addNotifyOnCheckedChangeListener(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.isChecked()) {

            setRadioButtonsEnabled(true);
        } else {
            setRadioButtonsEnabled(false);
        }

    }

    private void setRadioButtonsEnabled(boolean enabled) {
        int count = radioGroup_addNotificationType.getChildCount();
        for (int i = 0; i < count; i++) {
            View o = radioGroup_addNotificationType.getChildAt(i);
            if(o instanceof RadioButton) {
                o.setEnabled(enabled);
            }
        }
    }

    private void input_taskDateOnDateChangeListener(int year, int month, int dayOfMonth) {
        input_selectedDate = new GregorianCalendar(year, month, dayOfMonth);
    }

    private void input_taskTimeOnClick() {
        int hour = 0;
        int minute = 0;

        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) -> {
            String txtHour = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
            String txtMinute = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
            input_taskTime.setText(txtHour + ":" + txtMinute);
        }, hour, minute, true);

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void btn_addOnClick() {

        String taskName = input_taskName.getText().toString();
        String timeString = input_taskTime.getText().toString();
        if(taskName.isEmpty() || timeString.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Nazwa i godzina są wymagane", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            View view = toast.getView();
            view.setBackgroundColor(Color.RED);
            toast.show();
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setMessage(getString(R.string.add_promptOnAdd));
            dialogBuilder.setCancelable(true);

            dialogBuilder.setPositiveButton(
                    R.string.add_promptOnAddYes,
                    (dialog, id) -> {
                       saveTask();
                       dialog.cancel();
                    });

            dialogBuilder.setNegativeButton(
                    R.string.add_promptOnAddNo,
                    (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }

    private void saveTask() {
        String taskName = input_taskName.getText().toString();
        long datepicker = input_selectedDate.getTimeInMillis();
        Date datetime = new Date(datepicker);
        String timeString = input_taskTime.getText().toString();
        int hour = Integer.parseInt(timeString.substring(0,2));
        int minute = Integer.parseInt(timeString.substring(3,5));

        Time time = new Time(hour, minute, 0);

        boolean notify = switch_addNotify.isChecked();
        int checkedRadioButtonId = radioGroup_addNotificationType.getCheckedRadioButtonId();
        NotificationTypeEnum notificationType = NotificationTypeEnum.values()[checkedRadioButtonId];

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME, taskName);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE, datetime.toString());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME, time.toString());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFICATION_TYPE, String.valueOf(notificationType));
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NOTIFY, notify);

        long newRowId = writableDatabase.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
        Toast toast = Toast.makeText(getActivity(), R.string.add_addedToast, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }


}