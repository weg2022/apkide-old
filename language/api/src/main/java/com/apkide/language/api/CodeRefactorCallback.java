package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 代码重构回调
 */
public interface CodeRefactorCallback extends FileStoreCallback{
    
    void replaceText(@NonNull String filePath, int startLine, int startColumn,
                     int endLine, int endColumn, @NonNull String newText);
    
    void moveText(@NonNull String filePath, int startLine, int startColumn,
                  int endLine, int endColumn, int line, int column);
    
    void copyText(@NonNull String filePath, int startLine, int startColumn,
                  int endLine, int endColumn, int line, int column);
    
    void renameFile(@NonNull String filePath, @NonNull String newName);
    
    void moveFile(@NonNull String filePath, @NonNull String newFilePath);
    
    void createFile(@NonNull String filePath);
    
    void deleteFile(@NonNull String filePath);
    
    void warningFound(@NonNull String message);
    
    void refactoringNotSupported();
}
