package com.apkide.apktool.engine;

import androidx.annotation.NonNull;

import com.apkide.common.Logger;


public interface ApkDecodingListener {
    
    void apkDecodeStarted(@NonNull String apkFilePath);
    
    void apkDecodeProgressing(@NonNull Logger.Level level, @NonNull String msg);
    
    void apkDecodeFailed(@NonNull Throwable error);
    
    void apkDecodeFinish(@NonNull String outputPath);
}
