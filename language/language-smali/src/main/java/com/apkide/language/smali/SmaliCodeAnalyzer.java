package com.apkide.language.smali;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeAnalyzer;
import com.apkide.language.api.CodeAnalyzerCallback;

public class SmaliCodeAnalyzer implements CodeAnalyzer {
    private final SmaliLanguage myLanguage;
    
    public SmaliCodeAnalyzer(SmaliLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void analyze(@NonNull String filePath, @NonNull CodeAnalyzerCallback callback) {
    
    }
}
