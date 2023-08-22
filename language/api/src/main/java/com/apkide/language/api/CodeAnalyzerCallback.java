package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 代码分析回调
 */
public interface CodeAnalyzerCallback extends FileStoreCallback{

    void errorFound(@NonNull String source, @NonNull String message, @NonNull String code);
    void errorFound(@NonNull String filePath, @NonNull String message, @NonNull String code,
                    int startLine, int startColumn, int endLine, int endColumn);
    void warningFound(@NonNull String source, @NonNull String message, @NonNull String code);
    void warningFound(@NonNull String filePath, @NonNull String message, @NonNull String code,
                    int startLine, int startColumn, int endLine, int endColumn);
}
