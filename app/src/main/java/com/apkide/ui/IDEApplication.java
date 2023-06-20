package com.apkide.ui;

import androidx.multidex.MultiDexApplication;

public class IDEApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        App.initApp(getApplicationContext());
        AppPreferences.init(getApplicationContext());
    }
}
