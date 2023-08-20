package com.apkide.common.logger;

import androidx.annotation.NonNull;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public abstract class Logger {
    
    private static final Map<String, Logger> sLoggers = new Hashtable<>(5);
    private static LoggerFactory sFactory = name -> new Logger(name) {
        @Override
        protected void onLogging(@NonNull Level level, @NonNull String msg) {
        
        }
    };
    
    public static Logger getLogger(@NonNull String name) {
        if (sLoggers.containsKey(name)) {
            Logger logger = sLoggers.get(name);
            if (logger != null) {
                return logger;
            }
        }
        Logger logger = sFactory.createLogger(name);
        sLoggers.put(name, logger);
        return logger;
    }
    
    public static void setFactory(@NonNull LoggerFactory factory) {
        sFactory = factory;
    }
    
    private final String myName;
    private final List<LoggerListener> myListeners = new Vector<>(1);
    
    public Logger(String name) {
        this.myName = name;
    }
    
    @NonNull
    public String getName() {
        return myName;
    }
    
    public void addListener(@NonNull LoggerListener listener) {
        if (!myListeners.contains(listener))
            myListeners.add(listener);
    }
    
    public void removeListener(@NonNull LoggerListener listener) {
        myListeners.remove(listener);
    }
    
    public void debug(@NonNull String msg) {
        sendLog(Level.Debug, msg);
    }
    
    public void info(@NonNull String msg) {
        sendLog(Level.Information, msg);
    }
    
    public void verbose(@NonNull String msg) {
        sendLog(Level.Verbose, msg);
    }
    
    public void warning(@NonNull String msg) {
        sendLog(Level.Warning, msg);
    }
    
    public void error(@NonNull String msg) {
        sendLog(Level.Error, msg);
    }
    
    public void log(@NonNull Level level, @NonNull String msg) {
        sendLog(level, msg);
    }
    
    public void log(@NonNull Level level, @NonNull String msg, @NonNull Throwable ex) {
        sendLog(level, msg + "\nThrowable:" + ex);
    }
    
    private void sendLog(@NonNull Level level, @NonNull String msg) {
        onLogging(level, msg);
        for (LoggerListener listener : myListeners) {
            listener.logging(myName, level, msg);
        }
    }
    
    protected abstract void onLogging(@NonNull Level level, @NonNull String msg);
}
