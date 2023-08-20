package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.completion.Completion;
import com.apkide.ls.api.util.Position;

public interface CompleterCallback {
    void completeStarted(@NonNull String filePath, @NonNull Position position);
    
    void foundCompletion(@NonNull Completion completion);
    
    void completeCompleted(@NonNull String filePath);
}
