package com.apkide.openapi.language.api.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.api.FileEntry;

import org.antlr.v4.runtime.tree.ParseTree;

public interface SymbolSearcherCallback {
    void fileStarted(@NonNull FileEntry file);

    void elementFound(@NonNull FileEntry file, @NonNull ParseTree element);

    void fileFinished(@NonNull FileEntry file);
}
