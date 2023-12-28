package me.sussydeveloper.utils;

import lombok.Getter;

public enum LoggerUtils {

    INFO("\\u001B[36m[INFO]"),
    WARNING("\u001B[33m[WARN]"),
    ERROR("\u001B[31m[ERROR]"),
    DEBUG(" \u001B[35m[DEBUG]");

    @Getter
    private final String type;

    LoggerUtils(String type) {
        this.type = type;
    }

    public static void log(LoggerUtils type, String message){
        System.out.println(type.getType() + " -> " + message + "\u001B[0m");
    }
}
