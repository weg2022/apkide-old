package brut.util;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class Logger {

    private static final ArrayMap<String, Logger> sLoggerArrayMap = new ArrayMap<>();
    private static final List<LoggingListener> sLoggingListeners = new ArrayList<>();
    private final String fName;

    private Logger(String name) {
        fName = name;
    }

    public static Logger getInstance(@NonNull String name) {
        if (sLoggerArrayMap.containsKey(name)) {
            return sLoggerArrayMap.get(name);
        }
        Logger logger = new Logger(name);
        sLoggerArrayMap.put(name, logger);
        return logger;
    }

    public static void addListener(@NonNull LoggingListener listener) {
        if (sLoggingListeners.contains(listener)) return;
        sLoggingListeners.add(listener);
    }

    public static void removeListener(@NonNull LoggingListener listener) {
        sLoggingListeners.remove(listener);
    }

    public void info(String msg) {
        logging(Level.Info, msg);
    }

    public void info(String format, Object... args) {
        logging(Level.Info, String.format(format, args));
    }

    public void verbose(String msg) {
        logging(Level.Verbose, msg);
    }

    public void verbose(String format, Object... args) {
        logging(Level.Verbose, String.format(format, args));
    }

    public void warning(String msg) {
        logging(Level.Warning, msg);
    }

    public void warning(String format, Object... args) {
        logging(Level.Warning, String.format(format, args));
    }

    public void error(String msg) {
        logging(Level.Error, msg);
    }

    public void error(String format, Object... args) {
        logging(Level.Error, String.format(format, args));
    }

    private void logging(Level level, String msg) {
        for (LoggingListener listener : sLoggingListeners) {
            listener.logging(level, fName, msg);
        }
    }

    public enum Level {
        Info,
        Warning,
        Error,
        Verbose,
        Debug,
    }


    public interface LoggingListener {
        void logging(Level level, String logName, String message);
    }


}
