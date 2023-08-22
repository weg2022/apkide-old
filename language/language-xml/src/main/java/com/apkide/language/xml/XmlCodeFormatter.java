package com.apkide.language.xml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeFormatter;
import com.apkide.language.api.CodeFormatterCallback;

public class XmlCodeFormatter implements CodeFormatter {
    private final XmlLanguage myLanguage;
    
    public XmlCodeFormatter(XmlLanguage language) {
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
