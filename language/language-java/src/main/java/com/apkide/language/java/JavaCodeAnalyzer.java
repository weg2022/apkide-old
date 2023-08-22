package com.apkide.language.java;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeAnalyzer;
import com.apkide.language.api.CodeAnalyzerCallback;

public class JavaCodeAnalyzer implements CodeAnalyzer {
    private final JavaLanguage myLanguage;
    
    public JavaCodeAnalyzer(JavaLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void analyze(@NonNull String filePath, @NonNull CodeAnalyzerCallback callback) {
    
    }
}
