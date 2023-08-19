package com.apkide.ui.services.error;

import androidx.annotation.NonNull;

import com.apkide.codeanalysis.service.IDiagnosticListener;
import com.apkide.ls.api.Diagnostic;
import com.apkide.ui.App;
import com.apkide.ui.services.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorService implements IService {
    
    private final List<Diagnostic> myDiagnostics = new ArrayList<>();
    private final Map<String, List<Diagnostic>> myFileDiagnostics=new HashMap<>();
    
    @Override
    public void initialize() {
        App.getCodeEngineService().setDiagnosticListener(new IDiagnosticListener() {
            @Override
            public void reportDiagnostics(@NonNull List<Diagnostic> diagnostics) {
            
            }
            
            @Override
            public void reportFileDiagnostics(@NonNull String filePath, @NonNull List<Diagnostic> diagnostics) {
            
            }
        });
    }
    
    @Override
    public void shutdown() {
    
    }
}
