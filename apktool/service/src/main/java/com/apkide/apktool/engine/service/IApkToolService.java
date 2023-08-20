package com.apkide.apktool.engine.service;

import androidx.annotation.NonNull;

import com.apkide.apktool.engine.ApkToolConfig;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IApkToolService {
    
    void configure(@NonNull ApkToolConfig config);
    
    void build(@NonNull String rootPath, @NonNull String outputPath,@NonNull IProcessingCallback callback);
    
    void decode(@NonNull String apkFilePath, @NonNull String outputPath, @NonNull IProcessingCallback callback);
    
    void restart();
    
    void shutdown();
    
}
