package com.apkide.language.yaml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeAnalyzer;
import com.apkide.language.api.CodeAnalyzerCallback;

public class YamlCodeAnalyzer implements CodeAnalyzer {
    private final YamlLanguage myLanguage;
    
    public YamlCodeAnalyzer(YamlLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void analyze(@NonNull String filePath, @NonNull CodeAnalyzerCallback callback) {
    
    }
}
