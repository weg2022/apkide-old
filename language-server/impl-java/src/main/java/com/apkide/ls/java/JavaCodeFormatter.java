package com.apkide.ls.java;

import androidx.annotation.NonNull;

import com.apkide.ls.api.CodeFormatter;
import com.apkide.ls.api.util.Range;

public class JavaCodeFormatter implements CodeFormatter {
    private final JavaLanguageServer myLanguageServer;
    
    public JavaCodeFormatter(JavaLanguageServer languageServer) {
        myLanguageServer = languageServer;
    }
    
    @Override
    public void indent(@NonNull String filePath, int tabSize, int indentationSize) {
    
    }
    
    @Override
    public void indent(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range) {
    
    }
    
    @Override
    public void format(@NonNull String filePath, int tabSize, int indentationSize) {
    
    }
    
    @Override
    public void format(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range) {
    
    }
}
