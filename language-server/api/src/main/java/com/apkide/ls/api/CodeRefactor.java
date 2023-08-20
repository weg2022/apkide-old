package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Position;
import com.apkide.ls.api.util.Range;

public interface CodeRefactor {
    void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newName);
    
    void rename(@NonNull String filePath, @NonNull Position position, @NonNull String newName);
    
    void prepareInline(@NonNull String filePath, @NonNull Position position);
    
    void inline(@NonNull String filePath, @NonNull Position position);
    
    void safeDelete(@NonNull String filePath, @NonNull Position position);
    
    void surroundWith(@NonNull String filePath, @NonNull Position position);
    
    void comment(@NonNull String filePath, @NonNull Range range);
    
    void uncomment(@NonNull String filePath, @NonNull Range range);
}
