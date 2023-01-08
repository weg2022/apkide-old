package com.apkide.ui;

import androidx.multidex.MultiDexApplication;

import com.apkide.core.Core;

public class IDEApplication extends MultiDexApplication {
	@Override
	public void onCreate() {
		super.onCreate();
		Core.initialize(getApplicationContext());
	}
}
