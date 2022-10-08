package com.apkide.core.logging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class LoggerReceiver : BroadcastReceiver() {
	private var _loggerListener: LoggerListener? = null
	override fun onReceive(context: Context, intent: Intent) {
		val record = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			intent.getParcelableExtra("record", LogRecord::class.java)
		} else {
			intent.getParcelableExtra("record")
		}
		record ?: return
		if (DEBUG)
			Log.d(TAG, record.loggerName + ":" + record.level.toString() + ":" + record.message)
		
		if (_loggerListener != null) _loggerListener!!.onLogging(
			record.loggerName,
			record.level,
			record.loggerName,
			record.throwable
		)
	}
	
	fun setLoggerListener(loggerListener: LoggerListener?) {
		_loggerListener = loggerListener
	}
	
	interface LoggerListener {
		fun onLogging(logName: String?, level: Level?, msg: String?, thrown: Throwable?)
	}
	
	companion object {
		private const val TAG = "LoggerReceiver"
		private const val DEBUG = true
		const val ACTION_LOGGER = "ide.intent.action.logger"
	}
}