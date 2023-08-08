package com.apkide.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.apkide.common.app.Application;

public class AppPreferences {


	private static SharedPreferences preferences;

	public static void init(Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.preferences_application, false);
		PreferenceManager.setDefaultValues(context, R.xml.preferences_editor, false);
		PreferenceManager.setDefaultValues(context, R.xml.preferences_compiler, false);
		PreferenceManager.setDefaultValues(context, R.xml.preferences_sourcecontrol, false);
	}

	public static void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
		getPreferences().registerOnSharedPreferenceChangeListener(listener);
	}

	public static void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
		getPreferences().unregisterOnSharedPreferenceChangeListener(listener);
	}

	@NonNull
	public static SharedPreferences getPreferences() {
		if (preferences == null)
			preferences = PreferenceManager.getDefaultSharedPreferences(Application.get().getContext());
		return preferences;
	}

	@NonNull
	public static SharedPreferences.Editor getEditor() {
		return getPreferences().edit();
	}


	///////////////////////////////////////////////////////////////////////////
	// Editor Preferences
	///////////////////////////////////////////////////////////////////////////

	public static boolean isUseTabs() {
		return getPreferences().getBoolean("editor.useTabs", true);
	}

	public static int getTabSize() {
		return getPreferences().getInt("editor.tabSize", 4);
	}

	public static int getFontSize() {
		String value = getPreferences().getString("editor.fontSize", "16");
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 16;
		}
	}

}
