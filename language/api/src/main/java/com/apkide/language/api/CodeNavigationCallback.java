package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 代码导航回调
 */
public interface CodeNavigationCallback extends FileStoreCallback{
    
    
    void usageFound(@NonNull String filePath,int startLine,int startColumn,
                    int endLine,int endColumn);
    
    
    
    void symbolFound(int kind,@NonNull String name,boolean deprecated,
                     @NonNull String filePath,int startLine,int startColumn,
                     int endLine,int endColumn);
    
}
