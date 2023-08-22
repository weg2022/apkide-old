package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 格式化回调
 */
public interface CodeFormatterCallback extends FileStoreCallback {
    
    int getLineIndentation(int line);
    
    void indentationLine(int line, int indentationSize);
    
    int getIndentationSize();
    
    int getTabSize();
    
    int getLineCount();
    
    int getLineLength(int line);
    
    char getChar(int line, int column);
    
    int getStyle(int line, int column);
    
    void insertLineBreak(int line, int column);
    
    void removeLineBreak(int line);
    
    void insertText(int line, int column, @NonNull String text);
    
    void removeText(int startLine, int startColumn, int endLine, int endColumn);
    
}
