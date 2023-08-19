package com.apkide.apktool.engine.service;

import androidx.annotation.NonNull;

import com.apkide.apktool.engine.ApkToolConfig;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IApkToolService {
    
    void setApkBuildListener(@NonNull IApkBuildingListener listener);
    
    void setApkDecodeListener(@NonNull IApkDecodingListener listener);
    
    void configure(@NonNull ApkToolConfig config);
    
    void build(@NonNull String rootPath, @NonNull String outputPath);
    
    void decode(@NonNull String apkFilePath, @NonNull String outputPath);
    
    void restart();
    
    void shutdown();
    
}
