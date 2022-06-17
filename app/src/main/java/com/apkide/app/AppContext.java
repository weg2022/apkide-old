package com.apkide.app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class AppContext {
    public static Context getContext(){
        return AppContextProvider.getInstance().getContext();
    }

    public static String getString(int id){
        return getContext().getString(id);
    }

    public static SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

}
