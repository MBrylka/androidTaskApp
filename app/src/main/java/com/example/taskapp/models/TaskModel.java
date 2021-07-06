package com.example.taskapp.models;

import java.sql.Date;

public class TaskModel {
    private String taskName;
    private String taskDate;
    private String taskTime;

    public TaskModel(String taskName, String taskDate, String taskTime) {
        this.taskName = taskName;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }
}
