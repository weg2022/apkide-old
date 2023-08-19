package com.apkide.apktool.engine.service;

import androidx.annotation.NonNull;

import com.apkide.common.Logger;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IApkBuildingListener {
    
    void apkBuildStarted(@NonNull String rootPath);
    void apkBuildProgressing(@NonNull Logger.Level level, @NonNull String msg);
    
    void apkBuildFailed(@NonNull Throwable error);
    
    void apkBuildFinished(@NonNull String outputPath);
}
