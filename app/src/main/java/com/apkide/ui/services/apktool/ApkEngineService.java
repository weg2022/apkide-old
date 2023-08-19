package com.apkide.ui.services.apktool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.apkide.apktool.engine.service.ApkToolService;
import com.apkide.apktool.engine.service.IApkBuildingListener;
import com.apkide.apktool.engine.service.IApkDecodingListener;
import com.apkide.apktool.engine.service.IApkToolService;
import com.apkide.common.AppLog;
import com.apkide.common.Logger;
import com.apkide.ui.App;
import com.apkide.ui.services.IService;

import cn.thens.okbinder2.OkBinder;

public class ApkEngineService implements IService {
    
    private final ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = OkBinder.proxy(IApkToolService.class, service);
            AppLog.s(ApkEngineService.class, "Service Connected.");
            restart();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
            AppLog.s(ApkEngineService.class, "Service Disconnected.");
        }
    };
    
    private IApkToolService myService;
    
    public void decodeApk(@NonNull String apkFilePath,@NonNull String outputDir){
        if (isConnected()){
            myService.decode(apkFilePath,outputDir);
        }
    }
    
    public void buildApk(@NonNull String rootPath,@NonNull String outputDir){
        if (isConnected()){
            myService.build(rootPath,outputDir);
        }
    }
    
    public void setBuildListener(@NonNull IApkBuildingListener listener){
        if (isConnected()){
            myService.setApkBuildListener(listener);
        }
    }
    
    public void setDecodeListener(@NonNull IApkDecodingListener listener){
        if (isConnected()){
            myService.setApkDecodeListener(listener);
        }
    }
    
    public void restart() {
        if (!isConnected()) return;
        myService.restart();
        //Testing
        setDecodeListener(new IApkDecodingListener() {
            @Override
            public void apkDecodeStarted(@NonNull String apkFilePath) {
                AppLog.s(ApkEngineService.class,"apkDecodeStarted:"+apkFilePath);
            }
    
            @Override
            public void apkDecodeProgressing(@NonNull Logger.Level level, @NonNull String msg) {
                AppLog.s(ApkEngineService.class,"apkDecodeProgressing:"+msg);
            }
    
            @Override
            public void apkDecodeFailed(@NonNull Throwable error) {
                AppLog.s(ApkEngineService.class,"apkDecodeFailed:"+error.getMessage());
            }
    
            @Override
            public void apkDecodeFinish(@NonNull String outputPath) {
                AppLog.s(ApkEngineService.class,"apkDecodeFinish:"+outputPath);
            }
        });
    }
    
    @Override
    public void initialize() {
        bindService();
    }
    
    @Override
    public void shutdown() {
        unbindService();
    }
    
    public boolean isConnected() {
        return myService != null;
    }
    
    private void bindService() {
        if (App.getMainUI().bindService(new Intent(App.getMainUI(), ApkToolService.class),
                myConnection, Context.BIND_AUTO_CREATE)) {
            return;
        }
        AppLog.s(this, "bindService failed");
    }
    
    private void unbindService() {
        App.getMainUI().unbindService(myConnection);
    }
}
