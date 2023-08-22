package com.apkide.ui.services.project;

import androidx.annotation.NonNull;

import java.io.IOException;

public class ConfigureFileImpl extends ConfigureFile {
    
    public static final String APKTOOL_YML = "apktool.yml";
    
    public ConfigureFileImpl(@NonNull String filePath) {
        super(filePath);
    }
    
    
    @Override
    protected void onSync() throws IOException {
        if (!getFilePath().equals(APKTOOL_YML))
            throw new IOException(getFilePath()+" file not supported.");
        
        
    }
    
    @Override
    protected void onDestroy() {
    
    }
}
