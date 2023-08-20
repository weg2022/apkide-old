package com.apkide.ui.services.build;

import androidx.annotation.NonNull;

public interface BuildListener {
    void buildStarted(@NonNull String rootPath);
    
    void buildOutput(@NonNull String message);
    
    void buildFailed(@NonNull Throwable err);
    
    void buildFinished(@NonNull String outputPath);
}
