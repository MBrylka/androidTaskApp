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
import com.example.taskapp.feedEntries.TaskContract;

import java.sql.Time;
import java.sql.Date;

public class AddFragment extends Fragment {

    private AddViewModel addTaskViewModel;
    private View root;

    private EditText input_taskName;
    private EditText input_taskTime;
    private CalendarView input_taskDate;
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
        setListeners();

        taskDbHelper = new TaskDbHelper(getContext());
        writableDatabase = taskDbHelper.getWritableDatabase();

        return root;
    }

    private void initializeComponents() {
        btn_add = (Button) root.findViewById(R.id.btn_add);
        input_taskName = (EditText) root.findViewById(R.id.input_taskName);
        input_taskDate = (CalendarView) root.findViewById(R.id.calendar_taskDate);
        input_taskTime = root.findViewById(R.id.input_taskTime);
    }

    private void setListeners() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_addOnClick();
            }
        });
        input_taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_taskTimeOnClick();
            }
        });

        input_taskDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                input_taskDateOnDateChangeListener(year, month, dayOfMonth);
            }
        });
    }

    private void input_taskDateOnDateChangeListener(int year, int month, int dayOfMonth) {
        input_selectedDate = new GregorianCalendar(year, month, dayOfMonth);
    }

    private void input_taskTimeOnClick() {
        int hour = 0;
        int minute = 0;

        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String txtHour = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                String txtMinute = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                input_taskTime.setText(txtHour + ":" + txtMinute);

            }
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
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           saveTask();
                           dialog.cancel();
                        }
                    });

            dialogBuilder.setNegativeButton(
                    R.string.add_promptOnAddNo,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

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

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_NAME, taskName);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_DATE, datetime.toString());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TASK_TIME, time.toString());

        long newRowId = writableDatabase.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
        Toast toast = Toast.makeText(getActivity(), "Dodano nowe zadanie.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }


}