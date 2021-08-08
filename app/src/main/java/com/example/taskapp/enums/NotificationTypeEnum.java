package com.example.taskapp.enums;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public enum NotificationTypeEnum {
    MIN15("15 minut"),
    MIN30("30 minut"),
    HOUR1("1 godzina"),
    DAY1("1 dzień");

    String notificationType;

    private static final Map<String, NotificationTypeEnum> lookup = new HashMap<String, NotificationTypeEnum>();

    static {
        for(NotificationTypeEnum type: NotificationTypeEnum.values()) {
            lookup.put(type.getNotificationType(), type);
        }
    }

    NotificationTypeEnum(String s) {
        notificationType = s;
    }

    private String getNotificationType() {
        return this.notificationType;
    }
    public static NotificationTypeEnum get(String type) {
        return lookup.get(type);
    }

    @NonNull
    @Override
    public String toString() {
        return notificationType;
    }
}