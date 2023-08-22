package com.apkide.language.smali;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeHighlighter;
import com.apkide.language.api.CodeHighlighterCallback;

public class SmaliCodeHighlighter implements CodeHighlighter {
    private final SmaliLanguage myLanguage;
    
    public SmaliCodeHighlighter(SmaliLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void highlighting(@NonNull String filePath,
                             @NonNull CodeHighlighterCallback callback) {
    
    }
    
    @Override
    public void semanticHighlighting(@NonNull String filePath,
                                     @NonNull CodeHighlighterCallback callback) {
    
    }
}
