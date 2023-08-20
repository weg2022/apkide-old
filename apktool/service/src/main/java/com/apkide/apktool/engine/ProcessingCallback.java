package com.apkide.apktool.engine;

import androidx.annotation.NonNull;

public interface ProcessingCallback {
    
    void processPrepare(@NonNull String sourcePath);
    
    void processing(@NonNull String msg);
    
    void processError(@NonNull Throwable error);
    
    void processDone(@NonNull String outputPath);
    
}
