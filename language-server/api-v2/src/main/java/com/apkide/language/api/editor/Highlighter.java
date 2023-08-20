package com.apkide.language.api.editor;

import androidx.annotation.NonNull;

/**
 * 高亮接口
 */
public interface Highlighter {

    void highlighting(@NonNull String filePath,@NonNull HighlighterCallback callback);
    
    
}
