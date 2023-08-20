package com.apkide.apktool.engine.service;

import androidx.annotation.NonNull;

import com.apkide.common.Logger;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IProcessingCallback {
    
    void processPrepare(@NonNull String sourcePath);
    
    void processing(@NonNull Logger.Level level, @NonNull String msg);
    
    void processError(@NonNull Throwable error);
    
    void processDone(@NonNull String outputPath);
    
}
