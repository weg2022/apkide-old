package com.apkide.ui.services.project.apktool;

import androidx.annotation.NonNull;

import com.apkide.ui.services.project.util.Project;

import java.io.File;
import java.util.List;

public class ApkToolProject implements Project {
    
    private final String myRootPath;
    
    public ApkToolProject(String rootPath) {
        myRootPath = rootPath;
    }
    
    @NonNull
    @Override
    public String getRootPath() {
        return myRootPath;
    }
    
    @NonNull
    @Override
    public String getBuildPath() {
        return myRootPath+ File.separator+"build";
    }
    
    @NonNull
    @Override
    public List<String> getSourcePaths() {
        
        
        return null;
    }
}
