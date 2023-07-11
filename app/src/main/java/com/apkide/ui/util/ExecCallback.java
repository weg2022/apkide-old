package com.apkide.ui.util;

public interface ExecCallback<T> {
    void done(T result);

    void canceled();

    void failed(Throwable err);
}
