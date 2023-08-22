package com.apkide.language.smali;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeNavigation;
import com.apkide.language.api.CodeNavigationCallback;

public class SmaliCodeNavigation implements CodeNavigation {
    private final SmaliLanguage myLanguage;
    
    public SmaliCodeNavigation(SmaliLanguage language) {
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
