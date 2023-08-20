package com.apkide.ui.services.apktool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.apkide.apktool.engine.service.ApkToolService;
import com.apkide.apktool.engine.service.IApkToolService;
import com.apkide.apktool.engine.service.IProcessingCallback;
import com.apkide.common.AppLog;
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
    
    public void decodeApk(@NonNull String apkFilePath, @NonNull String outputDir, @NonNull IProcessingCallback callback) {
        if (isConnected()) {
            myService.decode(apkFilePath, outputDir, callback);
        }
    }
    
    public void buildApk(@NonNull String rootPath, @NonNull String outputDir, @NonNull IProcessingCallback callback) {
        if (isConnected()) {
            myService.build(rootPath, outputDir, callback);
        }
    }
    
    public void restart() {
        if (!isConnected()) return;
        myService.restart();
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
