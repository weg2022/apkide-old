package com.apkide.ui;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class IDEOptions {

    public static SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(ContentProvider.context);
    }

    public static int getLastVersionCode(){
        return getPreferences().getInt("versionCode", 1);
    }
    public static String getAppLanguage(){
        return getPreferences().getString("app.language", "system");
    }
    public static boolean isDarkMode(){
        return getPreferences().getBoolean("app.dark", false);
    }


}
