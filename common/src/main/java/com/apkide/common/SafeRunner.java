package com.apkide.common;

import androidx.annotation.NonNull;

public final class SafeRunner {

    public static void run(@NonNull SafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (runnable instanceof SafeRunnable2) {
                ((SafeRunnable2) runnable).handleException(e);
            } else
                throw new RuntimeException(e);
        }
    }


    public interface SafeRunnable {
        void run() throws Exception;
    }

    public interface SafeRunnable2 extends SafeRunnable {
        void handleException(@NonNull Throwable e);
    }
}
