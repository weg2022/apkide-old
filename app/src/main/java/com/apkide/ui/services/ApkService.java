package com.apkide.ui.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.apkide.apktool.engine.service.ApkToolService;
import com.apkide.apktool.engine.service.IApkToolService;
import com.apkide.apktool.engine.service.IProcessingCallback;
import com.apkide.ui.App;

import cn.thens.okbinder2.OkBinder;

public class ApkService implements AppService {
    private final ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = OkBinder.proxy(IApkToolService.class, service);
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (myService != null) {
                myService.shutdown();
                myService = null;
            }
        }
    };
    private IApkToolService myService;
    
    public void build(String rootPath,String outputPath,IProcessingCallback callback){
        if (myService!=null){
            myService.build(rootPath,outputPath,callback);
        }
    }
    
    public void decode(String apkFile, String outputPath, IProcessingCallback callback){
        if (myService!=null){
            myService.decode(apkFile,outputPath,callback);
        }
    }
    
    @Override
    public void initialize() {
        App.getMainUI().bindService(new Intent(App.getMainUI(), ApkToolService.class),
                myConnection, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    public void shutdown() {
        App.getMainUI().unbindService(myConnection);
    }
}
