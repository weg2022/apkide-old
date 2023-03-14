package com.apkide.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class AppPreferences {
	
	@SuppressLint("StaticFieldLeak")
	private static Context context;
	private static SharedPreferences preferences;
	public static void init(Context context){
		AppPreferences.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		PreferenceManager.setDefaultValues(context, R.xml.preferences_application, false);
		PreferenceManager.setDefaultValues(context, R.xml.preferences_editor, false);
		PreferenceManager.setDefaultValues(context, R.xml.preferences_compiler, false);
		PreferenceManager.setDefaultValues(context, R.xml.preferences_sourcecontrol, false);
	}
	
	
	@NonNull
	public static SharedPreferences getPreferences(){
		if (preferences==null)
			preferences=PreferenceManager.getDefaultSharedPreferences(context);
		return preferences;
	}
	
	public static boolean isForceBuildAll(){
		return getPreferences().getBoolean("forceBuildAll", false);
	}
	
	public static boolean isForceDeleteFramework(){
		return getPreferences().getBoolean("forceDeleteFramework", false);
	}
	
	public static boolean isDebugMode(){
		return getPreferences().getBoolean("debugMode", false);
	}
	
	public static boolean isNetSecConf(){
		return getPreferences().getBoolean("netSecConf", false);
	}
	
	public static boolean isVerbose(){
		return getPreferences().getBoolean("verbose", false);
	}
	
	public static boolean isCopyOriginalFiles(){
		return getPreferences().getBoolean("copyOriginalFiles", false);
	}
	
	public static boolean isUpdateFiles(){
		return getPreferences().getBoolean("updateFiles", false);
	}
	
	public static boolean isNoCrunch(){
		return getPreferences().getBoolean("noCrunch", false);
	}
	
	public static int getForceApi(){
		return getPreferences().getInt("forceApi", 0);
	}
	
	public static boolean isUseAapt2(){
		return getPreferences().getBoolean("useAapt2", false);
	}
	
	
}
