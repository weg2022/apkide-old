package com.apkide.common;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppLog {
	private static final String TAG = "Dev-A";

	public static void d(@NonNull String msg) {
		Log.d(TAG, msg);
	}

	public static void d(@Nullable Object obj) {
		Log.d(TAG, obj == null ? "null" : obj instanceof String ? (String) obj : obj.toString());
	}

	public static void d(int i) {
		Log.d(TAG, String.valueOf(i));
	}

	public static void s(@NonNull Object obj,@NonNull String method) {
		Log.i(TAG, obj.getClass().getName() + "." + method);
	}

	public static void s(@Nullable String state) {
		Log.i(TAG, state == null ? "null" : state);
	}

	public static void e(@NonNull Throwable t) {
		Log.e(TAG, t.toString(), t);
	}

	public static void e(@NonNull String msg) {
		Log.e(TAG, msg);
	}

	public static void e(@NonNull String msg,@NonNull Throwable t) {
		Log.e(TAG, msg, t);
	}

	public static void w(@NonNull String msg) {
		Log.w(TAG, msg);
	}

	public static void crash(@NonNull Throwable t) {
		Log.e(TAG, t.toString(), t);
	}
}
