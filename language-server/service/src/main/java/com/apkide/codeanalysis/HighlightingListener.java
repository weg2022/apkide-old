package com.apkide.codeanalysis;

import androidx.annotation.NonNull;

public interface HighlightingListener {
    
    void fileHighlighting(@NonNull String filePath, int[] styles,
                          int[] startLines, int[] startColumns,
                          int[] endLines, int[] endColumns, int size);
    
    void semanticHighlighting(@NonNull String filePath, int[] styles,
                              int[] startLines, int[] startColumns,
                              int[] endLines, int[] endColumns, int size);
}
