package com.apkide.common;

import androidx.annotation.NonNull;

public interface Command {
    @NonNull
    String getName();

    boolean isEnabled();

    boolean run();
}
