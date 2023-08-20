package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Location;
import com.apkide.ls.api.util.Position;

public interface FindUsagesCallback {
    
    void findUsagesStarted(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);
    
    void foundUsage(@NonNull Location location);
    
    void findUsagesCompleted(@NonNull String filePath);
    
}
