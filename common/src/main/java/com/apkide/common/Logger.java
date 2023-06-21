package com.apkide.common;

public abstract class Logger {

	private static final Object lock = new Object();
	private static Logger logger;

	public static Logger get() {
		synchronized (lock) {
			return logger;
		}
	}

	public static void set(Logger logger) {
		synchronized (lock) {
			Logger.logger = logger;
		}
	}

	public abstract void info(String msg);

	public abstract void warning(String msg);

	public abstract void error(String msg);
}
