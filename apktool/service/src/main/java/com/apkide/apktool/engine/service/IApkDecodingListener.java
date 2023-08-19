package com.apkide.apktool.engine.service;

import androidx.annotation.NonNull;

import com.apkide.common.Logger;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IApkDecodingListener {
    void apkDecodeStarted(@NonNull String apkFilePath);
    
    void apkDecodeProgressing(@NonNull Logger.Level level, @NonNull String msg);
    
    void apkDecodeFailed(@NonNull Throwable error);
    
    void apkDecodeFinish(@NonNull String outputPath);
}
