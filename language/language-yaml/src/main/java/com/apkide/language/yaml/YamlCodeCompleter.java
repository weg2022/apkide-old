package com.apkide.language.yaml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeCompleter;
import com.apkide.language.api.CodeCompleterCallback;

public class YamlCodeCompleter implements CodeCompleter {
    private final YamlLanguage myLanguage;
    
    public YamlCodeCompleter(YamlLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void completion(@NonNull String filePath, int line, int column, boolean allowTypes,
                           @NonNull CodeCompleterCallback callback) {
    
    }
}
