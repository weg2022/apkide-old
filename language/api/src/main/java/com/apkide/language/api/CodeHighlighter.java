package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 高亮接口
 */
public interface CodeHighlighter {
    
    void highlighting(@NonNull String filePath, @NonNull CodeHighlighterCallback callback);
    
    void semanticHighlighting(@NonNull String filePath, @NonNull CodeHighlighterCallback callback);
}
