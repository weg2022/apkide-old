package com.apkide.engine.service;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Diagnostic;

import java.util.List;

public interface IDiagnosticListener {
    
    void reportDiagnostics(@NonNull List<Diagnostic> diagnostics);
    
    void reportFileDiagnostics(@NonNull String filePath, @NonNull List<Diagnostic> diagnostics);
}
