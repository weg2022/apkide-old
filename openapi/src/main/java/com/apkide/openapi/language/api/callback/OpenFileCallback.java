package com.apkide.openapi.language.api.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.api.FileEntry;

import java.io.Reader;

public interface OpenFileCallback {

    @NonNull
    Reader getOpenFileReader(@NonNull FileEntry file);

    long getOpenFileVersion(@NonNull FileEntry file);

    long getOpenFileSize(@NonNull FileEntry file);

    boolean isOpenFile(@NonNull FileEntry file);

    void update();
}
