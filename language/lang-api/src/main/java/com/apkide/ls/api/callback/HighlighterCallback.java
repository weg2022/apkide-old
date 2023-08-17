package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Highlights;

public interface HighlighterCallback {
    void highlightStarted(@NonNull String filePath,long version);
    
    void fileHighlighting(@NonNull String filePath,long version, @NonNull Highlights highlights);
    
    void semanticHighlighting(@NonNull String filePath,long version, int style, int startLine, int startColumn, int endLine, int endColumn);
    
    void highlightFinished(@NonNull String filePath,long version);
}
