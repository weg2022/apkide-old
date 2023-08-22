package com.apkide.language.xml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeAnalyzer;
import com.apkide.language.api.CodeAnalyzerCallback;

public class XmlCodeAnalyzer implements CodeAnalyzer {
    private final XmlLanguage myLanguage;
    
    public XmlCodeAnalyzer(XmlLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void analyze(@NonNull String filePath, @NonNull CodeAnalyzerCallback callback) {
    
    }
}
