package com.apkide.apktool.engine;

import androidx.annotation.NonNull;

import com.apkide.common.Logger;

public interface ProcessingCallback {
    
    void processPrepare(@NonNull String sourcePath);
    
    void processing(@NonNull Logger.Level level, @NonNull String msg);
    
    void processError(@NonNull Throwable error);
    
    void processDone(@NonNull String outputPath);
    
}
