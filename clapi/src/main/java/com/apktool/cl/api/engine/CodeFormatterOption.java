package com.apktool.cl.api.engine;

import androidx.annotation.NonNull;

public interface CodeFormatterOption {
    String getGroupName();

    String getName();

    String getPreview(boolean preview);

    @NonNull
    String toString();
}
