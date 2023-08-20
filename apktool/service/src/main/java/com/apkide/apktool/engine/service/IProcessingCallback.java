package com.apkide.apktool.engine.service;

import androidx.annotation.NonNull;

import com.apkide.common.logger.Level;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IProcessingCallback {
    
    void processPrepare(@NonNull String sourcePath);
    
    void processing(@NonNull Level level, @NonNull String msg);
    
    void processError(@NonNull Throwable error);
    
    void processDone(@NonNull String outputPath);
    
}
