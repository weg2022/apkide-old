package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.highlighting.Highlights;

public interface HighlighterCallback {
    void highlightStarted(@NonNull String filePath);
    
    void foundHighlighting(@NonNull String filePath, @NonNull Highlights highlights);
    
    void foundSemantic(@NonNull String filePath, int style,
                       int startLine, int startColumn,
                       int endLine, int endColumn);
    
    void highlightCompleted(@NonNull String filePath);
}
