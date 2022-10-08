package com.apkide.core.logging

import android.content.Intent
import android.os.Handler
import com.apkide.core.Core
import java.util.function.Supplier

class Logger(val name: String) {
	
	@Volatile
	private var levelObject: Level? = null
	
	@Volatile
	private var levelValue: Int
	
	init {
		levelValue = Level.INFO.ordinal
	}
	
	private fun updateEffectiveLevel() {
		val level: Level? = if (levelObject != null) {
			levelObject
		} else {
			Level.INFO
		}
		val newLevelValue = level!!.intValue()
		if (levelValue == newLevelValue) return
		levelValue = newLevelValue
	}
	
	@set:Throws(SecurityException::class)
	var level: Level?
		get() = levelObject
		set(newLevel) {
			synchronized(treeLock) {
				levelObject = newLevel
				updateEffectiveLevel()
			}
		}
	
	fun isLoggable(level: Level): Boolean {
		return level.ordinal >= levelValue && levelValue != offValue
	}
	
	private fun doLog(record: LogRecord) {
		Handler.getMain().post {
			record.loggerName = name
			val intent = Intent()
			intent.action = LoggerReceiver.ACTION_LOGGER
			intent.putExtra("record", record)
			Core.context?.sendBroadcast(intent)
		}
	}
	
	fun log(level: Level, msg: String?, thrown: Throwable?) {
		if (!isLoggable(level)) {
			return
		}
		val lr = LogRecord(level, msg!!)
		lr.throwable = thrown
		doLog(lr)
	}
	
	fun log(level: Level, msg: String?) {
		if (!isLoggable(level)) {
			return
		}
		val record = LogRecord(level, msg!!)
		doLog(record)
	}
	
	fun log(level: Level, msgSupplier: Supplier<String?>) {
		if (!isLoggable(level)) {
			return
		}
		val record = LogRecord(level, msgSupplier.get()!!)
		doLog(record)
	}
	
	fun severe(msg: String?) {
		log(Level.SEVERE, msg)
	}
	
	fun warning(msg: String?) {
		log(Level.WARNING, msg)
	}
	
	fun info(msg: String?) {
		log(Level.INFO, msg)
	}
	
	fun config(msg: String?) {
		log(Level.CONFIG, msg)
	}
	
	fun fine(msg: String?) {
		log(Level.FINE, msg)
	}
	
	fun finer(msg: String?) {
		log(Level.FINER, msg)
	}
	
	fun finest(msg: String?) {
		log(Level.FINEST, msg)
	}
	
	fun severe(msgSupplier: Supplier<String?>) {
		log(Level.SEVERE, msgSupplier)
	}
	
	fun warning(msgSupplier: Supplier<String?>) {
		log(Level.WARNING, msgSupplier)
	}
	
	fun info(msgSupplier: Supplier<String?>) {
		log(Level.INFO, msgSupplier)
	}
	
	fun config(msgSupplier: Supplier<String?>) {
		log(Level.CONFIG, msgSupplier)
	}
	
	fun fine(msgSupplier: Supplier<String?>) {
		log(Level.FINE, msgSupplier)
	}
	
	fun finer(msgSupplier: Supplier<String?>) {
		log(Level.FINER, msgSupplier)
	}
	
	fun finest(msgSupplier: Supplier<String?>) {
		log(Level.FINEST, msgSupplier)
	}
	
	companion object {
		private val offValue = Level.OFF.ordinal
		private val treeLock = Any()
		@JvmStatic
		fun getLogger(logName: String): Logger {
			return Logger(logName)
		}
	}
}