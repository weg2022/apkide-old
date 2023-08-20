package com.apkide.common;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public abstract class Application {

	@NonNull
	public abstract File foundBinary(@NonNull String binaryName);

	@NonNull
	public abstract File foundFile(@NonNull String fileName);

	@NonNull
	public abstract File getDataDir();

	@NonNull
	public abstract File getCacheDir();

	@NonNull
	public abstract File getTempDir();
    
    @NonNull
    public abstract File getExternalDir();
    
    @NonNull
	public abstract Context getContext();
	
	public boolean postExec(@NonNull Runnable runnable) {
		return postExec(runnable,-1);
	}
	
	public abstract boolean postExec(@NonNull Runnable runnable,long delayMillis);
	
	public void syncExec(@NonNull Runnable workRun) {
		syncExec(workRun,null);
	}
	
	public abstract void syncExec(@NonNull Runnable workRun,@Nullable Runnable doneRun);
	
	private static final Object lock = new Object();

	private static Application sApplication;

	public static void set(@NonNull Application provider) {
		synchronized (lock) {
			sApplication = provider;
		}
	}

	@NonNull
	public static Application get() {
		synchronized (lock) {
			return sApplication;
		}
	}

}
