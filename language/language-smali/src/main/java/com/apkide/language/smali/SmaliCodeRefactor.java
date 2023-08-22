package com.apkide.language.smali;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeRefactor;
import com.apkide.language.api.CodeRefactorCallback;

public class SmaliCodeRefactor implements CodeRefactor {
    private final SmaliLanguage myLanguage;
    
    public SmaliCodeRefactor(SmaliLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void optimizeImports(@NonNull String filePath, @NonNull CodeRefactorCallback callback) {
    
    }
    
    @Override
    public void prepareRename(@NonNull String filePath, int line, int column,
                              @NonNull String newName, @NonNull CodeRefactorCallback callback) {
    
    }
    
    @Override
    public void rename(@NonNull String filePath, int line, int column,
                       @NonNull String newName, @NonNull CodeRefactorCallback callback) {
    
    }
    
    @Override
    public void prepareInline(@NonNull String filePath, int line, int column,
                              @NonNull CodeRefactorCallback callback) {
    
    }
    
    @Override
    public void inline(@NonNull String filePath, int line, int column,
                       @NonNull CodeRefactorCallback callback) {
    
    }
    
    @Override
    public void commentLines(@NonNull String filePath, int startLine, int startColumn,
                             int endLine, int endColumn, @NonNull CodeRefactorCallback callback) {
    
    }
    
    @Override
    public void removeCommentLines(@NonNull String filePath, int startLine, int startColumn,
                                   int endLine, int endColumn,
                                   @NonNull CodeRefactorCallback callback) {
    
    }
    
    @Override
    public void safeDelete(@NonNull String filePath, int line, int column,
                           @NonNull CodeRefactorCallback callback) {
    
    }
    
    @Override
    public void surroundWith(@NonNull String filePath, int line, int column,
                             @NonNull CodeRefactorCallback callback) {
    
    }
}
