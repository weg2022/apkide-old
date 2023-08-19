package com.apkide.ui.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.apkide.codeanalysis.service.CodeAnalysisService;
import com.apkide.codeanalysis.service.ICodeAnalysisService;
import com.apkide.codeanalysis.service.IDiagnosticListener;
import com.apkide.codeanalysis.service.IHighlightingListener;
import com.apkide.common.AppLog;
import com.apkide.ui.App;

import cn.thens.okbinder2.OkBinder;

public class EngineService implements IService {
    private final ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = OkBinder.proxy(ICodeAnalysisService.class, service);
            AppLog.s(EngineService.class, "Service Connected.");
            restart();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
            AppLog.s(EngineService.class, "Service Disconnected.");
        }
    };
    
    private ICodeAnalysisService myService;
    
    
    public void openFile(@NonNull String filePath) {
        if (!isConnected()) return;
        myService.openFile(filePath);
    }
    
    public void setHighlightingListener(IHighlightingListener listener) {
        if (!isConnected()) return;
        AppLog.s(this,"setHighlightingListener");
        myService.setHighlightingListener(listener);
    }
    
    public void setDiagnosticListener(IDiagnosticListener listener) {
        if (!isConnected()) return;
        myService.setDiagnosticListener(listener);
    }
    
    public void restart() {
        if (!isConnected()) return;
        myService.restart();
        App.getOpenFileService().register();
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
        if (App.getMainUI().bindService(
                new Intent(App.getMainUI(), CodeAnalysisService.class),
                myConnection, Context.BIND_AUTO_CREATE)) {
            return;
        }
        AppLog.s(this,"bindService failed");
    }
    
    private void unbindService() {
        App.getMainUI().unbindService(myConnection);
    }
}
