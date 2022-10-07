package com.apkide.ui.whatsnew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WhatsNewReceiver extends BroadcastReceiver {
	private static final String TAG = "WhatsNew";
	public static final String ACTION_WHATS_NEW="ide.intent.action.whatsnew";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals(ACTION_WHATS_NEW))return;
		
	}
	
	
}
