package com.apkide.openapi.language.api.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.api.FileEntry;

public interface UsageSearcherCallback {
    void searchStarted(@NonNull FileEntry file);

    void usageFound(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn);

    void searchFinished(@NonNull FileEntry file);
}
