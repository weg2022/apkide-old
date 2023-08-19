package com.apkide.codeanalysis.service;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Diagnostic;

import java.util.List;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IDiagnosticListener {
    
    void reportDiagnostics(@NonNull List<Diagnostic> diagnostics);
    
    void reportFileDiagnostics(@NonNull String filePath, @NonNull List<Diagnostic> diagnostics);
}
