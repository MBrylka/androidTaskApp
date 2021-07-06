package com.example.taskapp.models;

import java.sql.Date;

public class TaskModel {
    public String taskName;
    public String taskDate;
    public String taskTime;

    public TaskModel(String taskName, String taskDate, String taskTime) {
        this.taskName = taskName;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
    }


}
