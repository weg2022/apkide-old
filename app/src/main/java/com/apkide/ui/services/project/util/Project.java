package com.apkide.ui.services.project.util;

import androidx.annotation.NonNull;

import java.util.List;

public interface Project {
    
    
    @NonNull
    String getRootPath();
    
    @NonNull
    String getBuildPath();
    
    @NonNull
    List<String> getSourcePaths();
    
}
