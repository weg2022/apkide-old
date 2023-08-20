package com.apkide.common.logger;

import androidx.annotation.NonNull;

public interface LoggerListener {
    void logging(@NonNull String name, @NonNull Level level, @NonNull String msg);
}
