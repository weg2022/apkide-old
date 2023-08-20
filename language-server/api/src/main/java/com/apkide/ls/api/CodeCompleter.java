package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Position;

public interface CodeCompleter {
    void completion(@NonNull String filePath, @NonNull Position position);
}
