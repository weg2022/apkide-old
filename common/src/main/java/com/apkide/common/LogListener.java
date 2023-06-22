package com.apkide.common;

public interface LogListener {
    void loggingInfo(String name, String msg);

    void loggingError(String name, String msg);

    void loggingWarning(String name, String msg);
}
