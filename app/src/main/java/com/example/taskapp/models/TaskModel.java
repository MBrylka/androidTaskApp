package com.example.taskapp.models;

import com.example.taskapp.enums.NotificationTypeEnum;

import java.sql.Date;

public class TaskModel {
    private int id;
    private String taskName;
    private String taskDate;
    private String taskTime;
    private NotificationTypeEnum notificationType;
    private boolean notify;

    public TaskModel(int id, String taskName, String taskDate, String taskTime, NotificationTypeEnum notificationType, boolean notify) {
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

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public boolean getNotify() {
        return notify;
    }
}
