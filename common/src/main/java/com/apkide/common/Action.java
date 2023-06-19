package com.apkide.common;

import androidx.annotation.NonNull;

public interface Action {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    @NonNull
    String getName();

    void actionPerformed();
}
