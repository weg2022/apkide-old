package com.apkide.ui.views.editor;

import static java.util.Objects.requireNonNull;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class SyncRunner {
    private boolean myIsRunning;
    private final Handler myHandler;
    private final Runnable myRunnable;

    public synchronized void run() {
        if (myIsRunning) return;

        myIsRunning = true;
        myHandler.post(myRunnable);
    }


    public synchronized boolean isRunning() {
        synchronized (this) {
            return myIsRunning;
        }
    }

    public SyncRunner(@NonNull Runnable runnable) {
        myHandler = new Handler(requireNonNull(Looper.myLooper()));
        myRunnable = () -> {
            synchronized (SyncRunner.class) {
                myIsRunning = false;
            }
            runnable.run();
        };
    }

}
