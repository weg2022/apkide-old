package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * 文件存储回调
 */
public interface FileStoreCallback {
    @NonNull
    String getFileExtension(@NonNull String filePath);
    
    @NonNull
    String getFileName(@NonNull String filePath);
    
    @Nullable
    String getParentFilePath(@NonNull String filePath);
    
    @NonNull
    Reader getFileReader(@NonNull String filePath) throws IOException;
    
    long getFileSize(@NonNull String filePath);
    
    long getFileVersion(@NonNull String filePath);
    
    @NonNull
    List<String> getFileChildren(@NonNull String filePath);
    
    boolean isFileExists(@NonNull String filePath);
    
    boolean isDirectory(@NonNull String filePath);
    
    boolean isFile(@NonNull String filePath);
    
    boolean isArchiveFile(@NonNull String filePath);
    
    boolean isArchiveEntry(@NonNull String filePath);
    
    boolean isReadOnly(@NonNull String filePath);
}
