package com.apkide.common;

import android.content.Context;

import java.io.File;

public abstract class ApplicationProvider {

	public abstract File foundBinary(String binaryName);

	public abstract File foundFile(String fileName);

	public abstract File foundAndroidFrameworkFile();

	public abstract File getTempDirectory();

	public abstract Context getContext();

	private static final Object lock = new Object();

	private static ApplicationProvider applicationProvider;

	public static void set(ApplicationProvider provider) {
		synchronized (lock) {
			applicationProvider = provider;
		}
	}

	public static ApplicationProvider get() {
		synchronized (lock) {
			return applicationProvider;
		}
	}

}
