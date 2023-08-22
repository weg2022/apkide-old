package com.apkide.language.java;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeFormatter;
import com.apkide.language.api.CodeFormatterCallback;

public class JavaCodeFormatter implements CodeFormatter {
    private final JavaLanguage myLanguage;
    
    public JavaCodeFormatter(JavaLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void indentLines(@NonNull String filePath, int startLine, int startColumn,
                            int endLine, int endColumn, @NonNull CodeFormatterCallback callback) {
    
    }
    
    @Override
    public void formatLines(@NonNull String filePath, int startLine, int startColumn,
                            int endLine, int endColumn, @NonNull CodeFormatterCallback callback) {
        
    }
}
