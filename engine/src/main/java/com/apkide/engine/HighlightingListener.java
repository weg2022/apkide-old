package com.apkide.engine;

import androidx.annotation.NonNull;

public interface HighlightingListener {
    
    void fileHighlighting(@NonNull String filePath,long version, int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size);
    
    void semanticHighlighting(@NonNull String filePath,long version, int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size);
}
