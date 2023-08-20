package com.apkide.ls.java;

import androidx.annotation.NonNull;

import com.apkide.ls.api.CodeNavigation;
import com.apkide.ls.api.util.Position;

public class JavaCodeNavigation implements CodeNavigation {
    private final JavaLanguageServer myLanguageServer;
    
    public JavaCodeNavigation(JavaLanguageServer languageServer) {
        myLanguageServer = languageServer;
    }
    
    @Override
    public void findAPI(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration) {
    
    }
}
