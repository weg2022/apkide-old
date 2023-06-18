package com.apkide.common;

import androidx.annotation.NonNull;

public final class SafeRunner {
	
	public static void run(@NonNull SafeRunnerCallback runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			runnable.handleException(e);
		}
	}
	
	public interface SafeRunnerCallback {
		void handleException(@NonNull Throwable e);
		
		void run() throws Exception;
	}
}
