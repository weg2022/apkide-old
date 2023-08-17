package com.apkide.engine.service;

import androidx.annotation.NonNull;

public interface ICompilationListener {
    void compileError(@NonNull String msg);
    
}
