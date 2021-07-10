package com.example.taskapp.models;

import java.sql.Date;

public class TaskModel {
    private int id;
    private String taskName;
    private String taskDate;
    private String taskTime;
    private int notificationType;
    private boolean notify;

    private enum NotificationTypeEnum {
        MIN15,
        MIN30,
        HOUR1,
        DAY1;
    }

    public TaskModel(int id, String taskName, String taskDate, String taskTime, int notificationType, boolean notify) {
        this.id = id;
        this.taskName = taskName;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.notificationType = notificationType;
        this.notify = notify;
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
