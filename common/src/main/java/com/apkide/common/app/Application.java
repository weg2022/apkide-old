package com.apkide.common.app;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;

public abstract class Application {

	@NonNull
	public abstract File foundBinary(@NonNull String binaryName);

	@NonNull
	public abstract File foundFile(@NonNull String fileName);

	@NonNull
	public abstract File getTempDirectory();

	@NonNull
	public abstract Context getContext();

	private static final Object lock = new Object();

	private static Application sApplication;

	public static void set(Application provider) {
		synchronized (lock) {
			sApplication = provider;
		}
	}

	public static Application get() {
		synchronized (lock) {
			return sApplication;
		}
	}

}
