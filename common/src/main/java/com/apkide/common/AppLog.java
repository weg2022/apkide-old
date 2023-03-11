package com.apkide.common;

import android.util.Log;

import androidx.annotation.NonNull;

public final class AppLog {
	private static final String TAG = "APK-IDE";
	
	public static void crash(@NonNull Throwable th) {
		Log.e(TAG, th.toString(), th);
	}
	
	public static void d(Object obj) {
		Log.d(TAG, obj == null ? "null" : obj.toString());
	}
	
	public static void d(@NonNull String msg) {
		Log.d(TAG, msg);
	}
	
	public static void e(@NonNull String msg) {
		Log.e(TAG, msg);
	}
	
	public static void e(@NonNull String msg,@NonNull Throwable th) {
		Log.e(TAG, msg, th);
	}
	
	public static void e(@NonNull Throwable th) {
		Log.e(TAG, th.toString(), th);
	}
	
	public static void s(@NonNull Object obj,@NonNull String msg) {
		Log.i(TAG, obj.getClass().getName() + "." + msg);
	}
	
	public static void s(@NonNull String msg) {
		Log.i(TAG, msg);
	}
	
	public static void w(@NonNull String msg) {
		Log.w(TAG, msg);
	}
	
}
