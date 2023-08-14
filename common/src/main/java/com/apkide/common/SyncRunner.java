package com.apkide.common;

import static java.util.Objects.requireNonNull;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class SyncRunner {

    private boolean myIsRunning;
    private final long myDelayMillis;
    private final Handler myHandler;
    private final Runnable myRunnable;
    public synchronized void run() {
        if (myIsRunning) {
            return;
        }
        myIsRunning = true;
        if (myDelayMillis <= 0) {
            myHandler.post(myRunnable);
        } else {
            myHandler.postDelayed(myRunnable, myDelayMillis);
        }
    }

    public synchronized boolean isRunning() {
        synchronized (this) {
            return myIsRunning;
        }
    }

    public SyncRunner(@NonNull Runnable runnable){
        this(0,runnable);
    }

    public SyncRunner(long delayMillis, @NonNull Runnable runnable) {
        myHandler = new Handler(requireNonNull(Looper.myLooper()));
        myDelayMillis = delayMillis;
        myRunnable= () -> {
            synchronized (SyncRunner.class) {
                myIsRunning = false;
            }
            runnable.run();
        };
    }

}
