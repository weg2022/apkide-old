package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 代码分析接口
 */
public interface CodeAnalyzer {
    
    void analyze(@NonNull String filePath,@NonNull CodeAnalyzerCallback callback);
}
