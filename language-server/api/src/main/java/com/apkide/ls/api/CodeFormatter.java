package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Range;

public interface CodeFormatter {
    
    void indent(@NonNull String filePath, int tabSize, int indentationSize);
    
    void indent(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range);
    
    void format(@NonNull String filePath, int tabSize, int indentationSize);
    
    void format(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range);
    

}
