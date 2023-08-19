package com.apkide.apktool.engine.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.apktool.engine.ApkBuildingListener;
import com.apkide.apktool.engine.ApkDecodingListener;
import com.apkide.apktool.engine.ApkToolConfig;
import com.apkide.apktool.engine.ApkToolEngine;
import com.apkide.common.Logger;

import cn.thens.okbinder2.OkBinder;

public class ApkToolService extends Service {
    private ApkToolEngine myEngine = new ApkToolEngine();
    private final Binder myBinder = OkBinder.create(new IApkToolService() {
        @Override
        public void setApkBuildListener(@NonNull IApkBuildingListener listener) {
            myEngine.setApkBuildingListener(new ApkBuildingListener() {
                @Override
                public void apkBuildStarted(@NonNull String rootPath) {
                    listener.apkBuildStarted(rootPath);
                }
    
                @Override
                public void apkBuildProgressing(@NonNull Logger.Level level, @NonNull String msg) {
                    listener.apkBuildProgressing(level,msg);
                }
    
                @Override
                public void apkBuildFailed(@NonNull Throwable error) {
                    listener.apkBuildFailed(error);
                }
                
                @Override
                public void apkBuildFinished(@NonNull String outputPath) {
                    listener.apkBuildFinished(outputPath);
                }
            });
        }
        
        @Override
        public void setApkDecodeListener(@NonNull IApkDecodingListener listener) {
            myEngine.setApkDecodingListener(new ApkDecodingListener() {
                @Override
                public void apkDecodeStarted(@NonNull String apkFilePath) {
                    listener.apkDecodeStarted(apkFilePath);
                }
    
                @Override
                public void apkDecodeProgressing(@NonNull Logger.Level level, @NonNull String msg) {
                    listener.apkDecodeProgressing(level,msg);
                }
    
                @Override
                public void apkDecodeFailed(@NonNull Throwable error) {
                    listener.apkDecodeFailed(error);
                }
                
                @Override
                public void apkDecodeFinish(@NonNull String outputPath) {
                    listener.apkDecodeFinish(outputPath);
                }
            });
        }
    
        @Override
        public void configure(@NonNull ApkToolConfig config) {
            myEngine.configureConfig(config);
        }
    
        @Override
        public void build(@NonNull String rootPath, @NonNull String outputPath) {
            myEngine.build(rootPath,outputPath);
        }
    
        @Override
        public void decode(@NonNull String apkFilePath, @NonNull String outputPath) {
            myEngine.decode(apkFilePath,outputPath);
        }
    
        @Override
        public void restart() {
            myEngine.restart();
        }
        
        @Override
        public void shutdown() {
            myEngine.shutdown();
        }
    });
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        myEngine.destroy();
        myEngine = null;
    }
}
