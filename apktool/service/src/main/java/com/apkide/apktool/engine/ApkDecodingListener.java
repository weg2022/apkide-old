package com.apkide.apktool.engine;

import androidx.annotation.NonNull;

import com.apkide.common.logger.Level;


public interface ApkDecodingListener {
    
    void apkDecodeStarted(@NonNull String apkFilePath);
    
    void apkDecodeProgressing(@NonNull Level level, @NonNull String msg);
    
    void apkDecodeFailed(@NonNull Throwable error);
    
    void apkDecodeFinish(@NonNull String outputPath);
}
