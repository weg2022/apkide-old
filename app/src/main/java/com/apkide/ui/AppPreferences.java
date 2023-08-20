package com.apkide.ui;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.apkide.java.decompiler.main.extern.IFernflowerPreferences;

import java.util.Map;


public final class AppPreferences {
    
    private static SharedPreferences preferences;
    
    public static void initialize(Context context) {
        preferences = getDefaultSharedPreferences(context);
    }
    
    public static void registerListener(@NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getPreferences().registerOnSharedPreferenceChangeListener(listener);
    }
    
    public static void unregisterListener(@NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }
    
    @NonNull
    public static SharedPreferences getPreferences() {
        if (preferences == null)
            preferences = App.getPreferences();
        return preferences;
    }
    
    public static boolean isAnalyticsEnabled() {
        return getPreferences().getBoolean("app.analytics", true);
    }
    
    public static String getAppLanguage() {
        return getPreferences().getString("app.language", "default");
    }
    
    public static boolean isNightTheme() {
        return getPreferences().getBoolean("app.theme.night", false);
    }
    
    public static boolean isFollowSystemTheme() {
        return getPreferences().getBoolean("app.theme.followSystem", true);
    }
    
    public static Map<String, Object> getJavaBinaryReaderDefaultOptions() {
        //TODO: use sharedPreferences
        return IFernflowerPreferences.getDefaults();
    }
    
}
