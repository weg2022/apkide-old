package com.apkide.engine;

public final class Engine {
    private static final String TAG = "Engine";

    private final Object myLock = new Object();
    private boolean myShutdown;

    public Engine() {

        Thread thread = new Thread(null, () -> {
            while (!myShutdown) {
                try {


                    myLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Engine");
        thread.setPriority(Thread.MIN_PRIORITY + 1);
        thread.start();
    }


    public void shutdown() {
        myShutdown = true;
    }
}
