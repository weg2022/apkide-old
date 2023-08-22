package com.apkide.language.java;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeCompleter;
import com.apkide.language.api.CodeCompleterCallback;

public class JavaCodeCompleter implements CodeCompleter {
    private final JavaLanguage myLanguage;
    
    public JavaCodeCompleter(JavaLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void completion(@NonNull String filePath, int line, int column, boolean allowTypes,
                           @NonNull CodeCompleterCallback callback) {
        
    }
}
