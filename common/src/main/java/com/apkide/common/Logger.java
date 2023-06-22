package com.apkide.common;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

public  class Logger {

    private static final Object lock = new Object();
    private static Logger defaultLogger=new Logger("Default");
    private static final SafeListenerList<LogListener> listeners = new SafeListenerList<>();
    private static final ArrayMap<String, Logger> loggers = new ArrayMap<>();

    private final String name;

    private Logger(String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public  static void addListener(LogListener listener){
        listeners.add(listener);
    }

    public static void removeListener(LogListener listener){
        listeners.remove(listener);
    }

    @NonNull
    public static Logger get() {
        synchronized (lock) {
            return defaultLogger;
        }
    }

    public static void set(Logger logger) {
        synchronized (lock) {
            defaultLogger = logger;
        }
    }

    public static Logger get(String name) {
        synchronized (lock) {
            Logger logger = loggers.get(name);
            if (logger == null) {
                logger = new Logger(name) {
                };
                loggers.put(name, logger);
            }
            return logger;
        }
    }

    public static void set(String name, Logger logger) {
        synchronized (lock) {
            loggers.put(name, logger);
        }
    }

    public final void info(String msg) {
        onInfo(msg);
    }

    protected void onInfo(String msg){
        for (LogListener listener : listeners) {
            listener.loggingInfo(name,msg);
        }
    }

    public final void warning(String msg) {
        onWarning(msg);
    }

    protected void onWarning(String msg){
        for (LogListener listener : listeners) {
            listener.loggingWarning(name,msg);
        }
    }

    public final void error(String msg) {
        onError(msg);
    }

    protected void onError(String msg){
        for (LogListener listener : listeners) {
            listener.loggingError(name,msg);
        }
    }

}
