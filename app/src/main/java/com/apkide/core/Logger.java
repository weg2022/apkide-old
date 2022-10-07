package com.apkide.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.LogRecord;

public final class Logger {
	private static final int offValue = Level.OFF.ordinal();
	private static final Object treeLock = new Object();
	
	public static Logger getLogger(String logName) {
		return new Logger(logName);
	}
	
	private final String name;
	private volatile Level levelObject;
	
	private volatile int levelValue;
	
	public Logger(String name) {
		this.name = name;
		levelValue = Level.INFO.ordinal();
	}
	
	
	public void setLevel(Level newLevel) throws SecurityException {
		synchronized (treeLock) {
			levelObject = newLevel;
			updateEffectiveLevel();
		}
	}
	
	private void updateEffectiveLevel() {
		int newLevelValue;
		newLevelValue = Objects.requireNonNullElse(levelObject, Level.INFO).ordinal();
		if (levelValue == newLevelValue) {
			return;
		}
		
		levelValue = newLevelValue;
	}
	
	
	public Level getLevel() {
		return levelObject;
	}
	
	public boolean isLoggable(Level level) {
		return level.ordinal() >= levelValue && levelValue != offValue;
	}
	
	public String getName() {
		return name;
	}
	
	
	private void doLog(LogRecord record) {
		record.setLoggerName(name);
		Intent intent = new Intent();
		intent.setAction(LoggerReceiver.ACTION_LOGGER);
		intent.putExtra("record", record);
		Core.getContext().sendBroadcast(intent);
	}
	
	public void log(Level level, String msg, Throwable thrown) {
		if (!isLoggable(level)) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setThrowable(thrown);
		doLog(lr);
	}
	
	public void log(Level level, String msg) {
		if (!isLoggable(level)) {
			return;
		}
		LogRecord record=new LogRecord(level,msg);
		doLog(record);
	}
	
	
	public void log(Level level, Supplier<String> msgSupplier) {
		if (!isLoggable(level)) {
			return;
		}
		LogRecord record=new LogRecord(level,msgSupplier.get());
		doLog(record);
	}
	
	
	public void severe(String msg) {
		log(Level.SEVERE, msg);
	}
	
	
	public void warning(String msg) {
		log(Level.WARNING, msg);
	}
	
	
	public void info(String msg) {
		log(Level.INFO, msg);
	}
	
	
	public void config(String msg) {
		log(Level.CONFIG, msg);
	}
	
	
	public void fine(String msg) {
		log(Level.FINE, msg);
	}
	
	
	public void finer(String msg) {
		log(Level.FINER, msg);
	}
	
	
	public void finest(String msg) {
		log(Level.FINEST, msg);
	}
	
	
	public void severe(Supplier<String> msgSupplier) {
		log(Level.SEVERE, msgSupplier);
	}
	
	
	public void warning(Supplier<String> msgSupplier) {
		log(Level.WARNING, msgSupplier);
	}
	
	
	public void info(Supplier<String> msgSupplier) {
		log(Level.INFO, msgSupplier);
	}
	
	
	public void config(Supplier<String> msgSupplier) {
		log(Level.CONFIG, msgSupplier);
	}
	
	
	public void fine(Supplier<String> msgSupplier) {
		log(Level.FINE, msgSupplier);
	}
	
	
	public void finer(Supplier<String> msgSupplier) {
		log(Level.FINER, msgSupplier);
	}
	
	public void finest(Supplier<String> msgSupplier) {
		log(Level.FINEST, msgSupplier);
	}
	
	
	public enum Level {
		ALL("All"),
		CONFIG("Config"),
		FINE("Fine"),
		FINER("Finer"),
		FINEST("Finest"),
		INFO("Info"),
		OFF("Off"),
		SEVERE("Severe"),
		WARNING("Warning"),
		;
		public final String name;
		
		Level(String name) {
			this.name = name;
		}
	}
	
	public static class LogRecord implements Parcelable {
		private String loggerName;
		private Level level;
		private String message;
		
		private Throwable throwable;
		
		public LogRecord( Level level, String message) {
			this.level = level;
			this.message = message;
		}
		
		public void setLoggerName(String loggerName) {
			this.loggerName = loggerName;
		}
		
		public String getLoggerName() {
			return loggerName;
		}
		
		public void setLevel(Level level) {
			this.level = level;
		}
		
		public Level getLevel() {
			return level;
		}
		
		public void setMessage(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
		
		public void setThrowable(Throwable throwable) {
			this.throwable = throwable;
		}
		
		public Throwable getThrowable() {
			return throwable;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			LogRecord record = (LogRecord) o;
			return loggerName.equals(record.loggerName) && level == record.level && message.equals(record.message) && Objects.equals(throwable, record.throwable);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(loggerName, level, message, throwable);
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(this.loggerName);
			dest.writeInt(this.level == null ? -1 : this.level.ordinal());
			dest.writeString(this.message);
			dest.writeSerializable(this.throwable);
		}
		
		public void readFromParcel(Parcel source) {
			this.loggerName = source.readString();
			int tmpLevel = source.readInt();
			this.level = tmpLevel == -1 ? null : Level.values()[tmpLevel];
			this.message = source.readString();
			this.throwable = (Throwable) source.readSerializable();
		}
		
		protected LogRecord(Parcel in) {
			this.loggerName = in.readString();
			int tmpLevel = in.readInt();
			this.level = tmpLevel == -1 ? null : Level.values()[tmpLevel];
			this.message = in.readString();
			this.throwable = (Throwable) in.readSerializable();
		}
		
		public static final Creator<LogRecord> CREATOR = new Creator<>() {
			@Override
			public LogRecord createFromParcel(Parcel source) {
				return new LogRecord(source);
			}
			
			@Override
			public LogRecord[] newArray(int size) {
				return new LogRecord[size];
			}
		};
	}
}
