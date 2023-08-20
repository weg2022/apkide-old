package com.apkide.ui.browsers.error;

import com.apkide.ls.api.diagnostic.Diagnostic;
import com.apkide.ui.util.Entry;

public class ErrorEntry implements Entry, Comparable<ErrorEntry> {
    
    
    private Diagnostic myDiagnostic;
    
    
    public boolean isDiagnostic() {
        return myDiagnostic != null;
    }
    
    public Diagnostic getDiagnostic() {
        return myDiagnostic;
    }
    
    @Override
    public int compareTo(ErrorEntry o) {
        if (isDiagnostic() && o.isDiagnostic()) {
            if (getDiagnostic().source.equals(o.getDiagnostic().source)) {
                return getDiagnostic().range.compareTo(o.getDiagnostic().range);
            }
        }
        
        return 0;
    }
}
