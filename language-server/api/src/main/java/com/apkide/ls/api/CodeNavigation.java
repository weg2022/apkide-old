package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Position;

public interface CodeNavigation {
    void findAPI(@NonNull String filePath, @NonNull Position position);
    
    void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);
}
