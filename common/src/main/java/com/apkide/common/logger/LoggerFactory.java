package com.apkide.common.logger;

import androidx.annotation.NonNull;

public interface LoggerFactory {
    @NonNull
    Logger createLogger(@NonNull String name);
}
