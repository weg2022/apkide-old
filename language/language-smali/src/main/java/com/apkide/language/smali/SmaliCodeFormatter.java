package com.apkide.language.smali;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeFormatter;
import com.apkide.language.api.CodeFormatterCallback;

public class SmaliCodeFormatter implements CodeFormatter {
    private final SmaliLanguage myLanguage;
    
    public SmaliCodeFormatter(SmaliLanguage language) {
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
