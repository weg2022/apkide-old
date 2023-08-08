package com.apkide.common;

import androidx.annotation.NonNull;

public final class SafeRunner {

	public static void run(@NonNull SafeRunnable runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			runnable.handleException(e);
		}
	}


	public interface SafeRunnable {
		void run() throws Exception;

		default void handleException(@NonNull Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
