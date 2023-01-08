package com.apkide.core;

import android.annotation.SuppressLint;
import android.content.Context;


public final class Core {
	public static final String VERSION = "2.7.0";

	@SuppressLint("StaticFieldLeak")
	private static Context context;
	public static void initialize(Context context) {
		Core.context=context;
	}

	public static Context getContext() {
		return context;
	}

}
