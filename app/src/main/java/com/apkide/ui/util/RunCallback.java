package com.apkide.ui.util;

public interface RunCallback {
    void done();

    void cancel();

    void fail(Throwable err);
}
