package com.apkide.apktool.engine;

import androidx.annotation.NonNull;

import com.apkide.common.logger.Level;


public interface ApkBuildingListener {
    void apkBuildStarted(@NonNull String rootPath);
    void apkBuildProgressing(@NonNull Level level, @NonNull String msg);
    
    void apkBuildFailed(@NonNull Throwable error);
    
    void apkBuildFinished(@NonNull String outputPath);
}
