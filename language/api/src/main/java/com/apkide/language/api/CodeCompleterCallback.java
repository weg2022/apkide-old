package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 代码补全回调
 */
public interface CodeCompleterCallback extends FileStoreCallback{
    
    void completionFound(int kind, @NonNull String label, @Nullable String details,
                         @Nullable String documentation, boolean deprecated, boolean preselect,
                         @Nullable String sortText, @NonNull String insertText, boolean snippet);
    
}
