package com.apkide.analysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public abstract class PluginContext {

    private static final Object sLock = new Object();

    private static PluginContext sContext;

    public static void set(PluginContext context) {
        synchronized (sLock) {
            sContext = context;
        }
    }

    public static PluginContext get() {
        synchronized (sLock) {
            return sContext;
        }
    }


    public abstract String getString(@StringRes int id);

    @Nullable
    public <T> T getOption(@NonNull String key) {
        return getOption(key, null);
    }

    @Nullable
    public abstract <T> T getOption(@NonNull String key, @Nullable Object defValue);

    public abstract void setOption(@NonNull String key, @Nullable Object value);

}
