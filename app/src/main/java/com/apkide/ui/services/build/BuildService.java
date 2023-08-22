package com.apkide.ui.services.build;

import androidx.annotation.NonNull;

import com.apkide.ui.services.AppService;

public class BuildService implements AppService {
    
    
    private BuildListener myListener;
    
    @Override
    public void initialize() {
    
    }
    
    public void setListener(@NonNull BuildListener listener) {
        myListener = listener;
    }
    
    public void build() {
    
    }
    
    @Override
    public void shutdown() {
    
    }
}
