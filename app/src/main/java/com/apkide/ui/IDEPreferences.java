package com.apkide.ui;

import static com.apkide.app.AppContext.getPreferences;


public final class IDEPreferences {

    public static String getAppTheme(){
        return getPreferences().getString("appTheme", "default-light");
    }

    public static boolean isDarkTheme(){
        return getAppTheme().contains("dark");
    }

    public static String getAppLanguage(){
        return getPreferences().getString("appLanguage", "system");
    }



}
