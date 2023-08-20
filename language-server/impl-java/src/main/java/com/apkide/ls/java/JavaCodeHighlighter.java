package com.apkide.ls.java;

import androidx.annotation.NonNull;

import com.apkide.ls.api.CodeHighlighter;

public class JavaCodeHighlighter implements CodeHighlighter {
    private final JavaLanguageServer myLanguageServer;
    
    public JavaCodeHighlighter(JavaLanguageServer languageServer) {
        myLanguageServer = languageServer;
    }
    
    @Override
    public void highlighting(@NonNull String filePath) {
    
    }
}
