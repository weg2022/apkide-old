package com.apkide.ui;

import static androidx.preference.PreferenceManager.setDefaultValues;

import android.annotation.SuppressLint;
import android.content.Context;

public class IDEPreferences {
	
	@SuppressLint("StaticFieldLeak")
	private static Context sContext;
	
	
	public static void init(Context context) {
		sContext = context;
		setDefaultValues(context, R.xml.application_preferences, false);
		setDefaultValues(context, R.xml.editor_preferences, false);
		setDefaultValues(context, R.xml.compiler_preferences, false);
		setDefaultValues(context, R.xml.sourcecontrol_preferences, false);
	}
}
