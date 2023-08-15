package com.apkide.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ProgressMonitor<P, R> {
    
    void begin();
    
    void progress(@Nullable P value);
    
    void fail(@NonNull Throwable err);
    
    void done(@Nullable R value);
    
    void cancel(boolean canceled);
}
