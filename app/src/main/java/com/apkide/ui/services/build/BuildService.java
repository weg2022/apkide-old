package com.apkide.ui.services.build;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.ui.App;
import com.apkide.ui.services.AppService;

import java.util.List;
import java.util.Vector;

public class BuildService implements AppService {
    
    private final List<BuildListener> myListeners = new Vector<>(1);
    
    private final Object myLock = new Object();
    private boolean myShutdown;
    private boolean myBuilding;
    private String mySourcePath;
    private String myOutputPath;
    
    @Override
    public void initialize() {
        Thread thread = new Thread(null, () -> {
            try {
                
                synchronized (myLock) {
                    while (!myShutdown) {
                        
                        if (myBuilding && mySourcePath != null && myOutputPath != null) {
                        
                        
                        
                        }
                        myLock.wait();
                    }
                }
            } catch (InterruptedException e) {
                AppLog.e(e);
            }
        }, "BuildService");
        thread.start();
    }
    
    
    public void addListener(@NonNull BuildListener listener) {
        if (!myListeners.contains(listener))
            myListeners.add(listener);
    }
    
    public void removeListener(@NonNull BuildListener listener) {
        myListeners.remove(listener);
    }
    
    
    public void build() {
        if (!isBuildable()) return;
        
        
        synchronized (myLock) {
            myBuilding = true;
            myLock.notify();
        }
    }
    
    
    public boolean isBuildable() {
        return App.getProjectService().isProjectOpened();
    }
    
    @Override
    public void shutdown() {
        synchronized (myLock) {
            myShutdown = true;
            myLock.notify();
        }
    }
}
