package com.apkide.language.java;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeNavigation;
import com.apkide.language.api.CodeNavigationCallback;

public class JavaCodeNavigation implements CodeNavigation {
    private final JavaLanguage myLanguage;
    
    public JavaCodeNavigation(JavaLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void searchUsages(@NonNull String filePath, int line, int column,
                             @NonNull CodeNavigationCallback callback) {
    
    }
    
    @Override
    public void searchSymbol(@NonNull String filePath, int line, int column,
                             boolean includeDeclaration,
                             @NonNull CodeNavigationCallback callback) {
    
    }
}
