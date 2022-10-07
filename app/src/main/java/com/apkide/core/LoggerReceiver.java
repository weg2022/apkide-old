package com.apkide.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LoggerReceiver extends BroadcastReceiver {
	private static final String TAG = "LoggerReceiver";
	public final static String ACTION_LOGGER = "ide.intent.action.logger";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals(ACTION_LOGGER))return;
		Logger.LogRecord record = intent.getParcelableExtra("record");
		Log.d(TAG, record.getLoggerName() + ":" + record.getLevel().name + ":" + record.getMessage());
	}
}
