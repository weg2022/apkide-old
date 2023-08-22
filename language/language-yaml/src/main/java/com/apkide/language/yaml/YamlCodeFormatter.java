package com.apkide.language.yaml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeFormatter;
import com.apkide.language.api.CodeFormatterCallback;

public class YamlCodeFormatter implements CodeFormatter {
    private final YamlLanguage myLanguage;
    
    public YamlCodeFormatter(YamlLanguage language) {
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
