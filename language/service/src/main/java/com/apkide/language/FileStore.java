package com.apkide.language;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public abstract class FileStore {
    
    private static final Object sLock = new Object();
    private static FileStore sStore;
    
    public static void set(FileStore store) {
        synchronized (sLock) {
            sStore = store;
        }
    }
    
    public static FileStore get() {
        synchronized (sLock) {
            if (sStore == null)
                throw new RuntimeException("FileStore not implementation");
            return sStore;
        }
    }
    
    public abstract boolean matchFilePatterns(@NonNull String filePath,
                                              @NonNull String filePattern);
    
    @NonNull
    public abstract String getFileExtension(@NonNull String filePath);
    
    @NonNull
    public abstract String getFileName(@NonNull String filePath);
    
    @Nullable
    public abstract String getParentFilePath(@NonNull String filePath);
    
    @NonNull
    public abstract Reader getFileReader(@NonNull String filePath) throws IOException;
    
    public abstract long getFileSize(@NonNull String filePath);
    
    public abstract long getFileVersion(@NonNull String filePath);
    
    @NonNull
    public abstract List<String> getFileChildren(@NonNull String filePath);
    
    public abstract boolean isFileExists(@NonNull String filePath);
    
    public abstract boolean isDirectory(@NonNull String filePath);
    
    public abstract boolean isFile(@NonNull String filePath);
    
    public abstract boolean isArchiveFile(@NonNull String filePath);
    
    public abstract boolean isArchiveEntry(@NonNull String filePath);
    
    public abstract boolean isReadOnly(@NonNull String filePath);
}
