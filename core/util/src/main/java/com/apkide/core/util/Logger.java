package com.apkide.core.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Logger {

	private static final Map<String, Logger> loggerMap = new HashMap<>();
	private static final Vector<LoggingListener> loggingListeners = new Vector<>();

	public static boolean printLogging=true;

	public static Logger getLogger(String name) {
		Logger logger = loggerMap.get(name);
		if (logger == null) {
			logger = new Logger(name);
			loggerMap.put(name, logger);
		}
		return logger;
	}

	private final String name;

	private Logger(String name) {
		this.name = name;
	}

	public boolean isLogPrintable() {
		return printLogging;
	}

	public void severe(String message) {
		if (isLogPrintable())
			Log.d(name, message);
		if (!loggingListeners.isEmpty()) {
			for (LoggingListener listener : loggingListeners) {
				listener.severe(name, message);
			}
		}
	}

	public void severe(String message,Throwable e) {
		if (isLogPrintable())
			Log.d(name, message,e);
		if (!loggingListeners.isEmpty()) {
			for (LoggingListener listener : loggingListeners) {
				listener.severe(name, message,e);
			}
		}
	}

	public void fine(String message) {
		if (isLogPrintable()) {
			Log.d(name, message);
		}
		if (!loggingListeners.isEmpty()) {
			for (LoggingListener listener : loggingListeners) {
				listener.fine(name, message);
			}
		}
	}

	public void warning(String message) {
		if (isLogPrintable())
			Log.w(name, message);
		if (!loggingListeners.isEmpty()) {
			for (LoggingListener listener : loggingListeners) {
				listener.warning(name, message);
			}
		}
	}

	public void warning(String message,Throwable e) {
		if (isLogPrintable())
			Log.w(name, message,e);
		if (!loggingListeners.isEmpty()) {
			for (LoggingListener listener : loggingListeners) {
				listener.warning(name, message,e);
			}
		}
	}

	public void info(String message) {
		if (isLogPrintable())
			Log.i(name, message);
		if (!loggingListeners.isEmpty()) {
			for (LoggingListener listener : loggingListeners) {
				listener.info(name, message);
			}
		}
	}


	public static void addLoggingListener(LoggingListener listener) {
		if (listener != null && !loggingListeners.contains(listener)) {
			loggingListeners.add(listener);
		}
	}

	public static void removeLoggingListener(LoggingListener listener) {
		if (listener != null)
			loggingListeners.remove(listener);
	}

	public interface LoggingListener {
		void info(String name, String msg);

		void fine(String name, String msg);

		void warning(String name, String msg);

		void severe(String name, String msg);

		void warning(String name, String message, Throwable e);

		void severe(String name, String message, Throwable e);
	}
}
