package com.apkide.apktool.engine.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.apktool.engine.ApkToolConfig;
import com.apkide.apktool.engine.ApkToolEngine;
import com.apkide.apktool.engine.ProcessingCallback;
import com.apkide.common.Logger;

import cn.thens.okbinder2.OkBinder;

public class ApkToolService extends Service {
    private ApkToolEngine myEngine = new ApkToolEngine();
    private final Binder myBinder = OkBinder.create(new IApkToolService() {
        
        @Override
        public void configure(@NonNull ApkToolConfig config) {
            myEngine.configureConfig(config);
        }
        
        @Override
        public void build(@NonNull String rootPath, @NonNull String outputPath, @NonNull IProcessingCallback callback) {
            myEngine.build(rootPath, outputPath, new ProcessingCallback() {
                @Override
                public void processPrepare(@NonNull String sourcePath) {
                    callback.processPrepare(sourcePath);
                }
    
                @Override
                public void processing(@NonNull Logger.Level level, @NonNull String msg) {
                    callback.processing(level, msg);
                }
    
                @Override
                public void processError(@NonNull Throwable error) {
                    callback.processError(error);
                }
    
                @Override
                public void processDone(@NonNull String outputPath) {
                    callback.processDone(outputPath);
                }
            });
        }
        
        @Override
        public void decode(@NonNull String apkFilePath, @NonNull String outputPath, @NonNull IProcessingCallback callback) {
            myEngine.decode(apkFilePath, outputPath, new ProcessingCallback() {
                @Override
                public void processPrepare(@NonNull String sourcePath) {
                    callback.processPrepare(sourcePath);
                }
    
                @Override
                public void processing(@NonNull Logger.Level level, @NonNull String msg) {
                    callback.processing(level, msg);
                }
    
                @Override
                public void processError(@NonNull Throwable error) {
                    callback.processError(error);
                }
    
                @Override
                public void processDone(@NonNull String outputPath) {
                    callback.processDone(outputPath);
                }
            });
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
