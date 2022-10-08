package com.apkide.core.logging

import android.os.Parcel
import android.os.Parcelable
import java.util.Objects

class LogRecord(var level: Level, var message: String) : Parcelable {
	var loggerName: String? = null
	var throwable: Throwable? = null
	
	constructor(parcel: Parcel) : this(
		Level.values()[parcel.readInt()],
		parcel.readString()
	) {
		level = Level.values()[parcel.readInt()]
		loggerName = parcel.readString()
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		val record = other as LogRecord
		return loggerName == record.loggerName && level === record.level && message == record.message && throwable == record.throwable
	}
	
	override fun hashCode(): Int {
		return Objects.hash(loggerName, level, message, throwable)
	}
	
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(message)
		parcel.writeString(loggerName)
	}
	
	override fun describeContents(): Int {
		return 0
	}
	
	companion object CREATOR : Parcelable.Creator<LogRecord> {
		override fun createFromParcel(parcel: Parcel): LogRecord {
			return LogRecord(parcel)
		}
		
		override fun newArray(size: Int): Array<LogRecord?> {
			return arrayOfNulls(size)
		}
	}
}