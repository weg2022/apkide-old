package com.apkide.ui.services;

import com.apkide.common.AppLog;

public class AppService {

    private final Object lock = new Object();
    private boolean shutdown;

    public AppService() {
        Thread thread = new Thread(null, () -> {
            try {
                while (!shutdown) {


                    synchronized (lock) {
                        lock.wait();
                    }
                }
            } catch (InterruptedException e) {
                AppLog.e(e);
            }
        }, "Engine", 2000000L);
        thread.setPriority(Thread.MIN_PRIORITY + 1);
        thread.start();
    }


    public void shutdown() {
        synchronized (lock) {
            shutdown = true;
            lock.notify();
        }
    }
}
