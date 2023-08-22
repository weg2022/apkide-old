package com.apkide.language.smali;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeCompleter;
import com.apkide.language.api.CodeCompleterCallback;

public class SmaliCodeCompleter implements CodeCompleter {
    private final SmaliLanguage myLanguage;
    
    public SmaliCodeCompleter(SmaliLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void completion(@NonNull String filePath, int line, int column, boolean allowTypes,
                           @NonNull CodeCompleterCallback callback) {
    
    }
}
