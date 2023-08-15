package com.apkide.ui.services.project;

import androidx.annotation.NonNull;

import com.apkide.ui.services.project.util.ConfigureFile;
import com.apkide.ui.services.project.util.ConfigureFileException;

public class IDEConfigureFile extends ConfigureFile {
    
    public static final String FILE_EXTENSION = ".project.xml";
    
    public IDEConfigureFile(@NonNull String filePath) {
        super(filePath);
    }
    
    
    @Override
    protected void onSync() throws ConfigureFileException {
        if (!getFilePath().endsWith(FILE_EXTENSION))
            throw new ConfigureFileException(getFilePath()+" file not supported.");
        
        
    }
    
    @Override
    protected void onDestroy() {
    
    }
}
