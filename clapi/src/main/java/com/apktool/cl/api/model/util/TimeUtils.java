package com.apktool.cl.api.model.util;

import java.util.concurrent.atomic.AtomicInteger;

public class TimeUtils {
    private static final long INITIAL_TIME = System.currentTimeMillis();
    private static final AtomicInteger timeDelta = new AtomicInteger(0);

    static {
        Thread thread = new Thread("Time Getter") {
            @Override
            public void run() {
                try {
                    while (!this.isInterrupted()) {
                        TimeUtils.timeDelta.set((int) ((System.currentTimeMillis() - TimeUtils.INITIAL_TIME) / 100L));
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };
        thread.setPriority(1);
        thread.start();
    }

    TimeUtils() {
        super();
    }

    public static long currentTimeMillis() {
        return INITIAL_TIME + ((long) timeDelta.get() & 4294967295L) * 100L;
    }
}

