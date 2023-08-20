package com.apkide.ls.java;

import androidx.annotation.NonNull;

import com.apkide.ls.api.CodeCompleter;
import com.apkide.ls.api.util.Position;

public class JavaCodeCompleter implements CodeCompleter {
    private final JavaLanguageServer myLanguageServer;
    
    public JavaCodeCompleter(JavaLanguageServer languageServer) {
        myLanguageServer = languageServer;
    }
    
    @Override
    public void completion(@NonNull String filePath, @NonNull Position position) {
    
    }
}
