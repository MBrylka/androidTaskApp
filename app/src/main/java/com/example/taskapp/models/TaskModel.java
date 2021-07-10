package com.example.taskapp.models;

import java.sql.Date;

public class TaskModel {
    private int id;
    private String taskName;
    private String taskDate;
    private String taskTime;

    public TaskModel(int id, String taskName, String taskDate, String taskTime) {
        this.id = id;
        this.taskName = taskName;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
    }

    public int getId() {
        return id;
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
