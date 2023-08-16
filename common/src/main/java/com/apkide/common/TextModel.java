package com.apkide.common;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import java.io.Reader;

public interface TextModel extends Cloneable {
    
    interface TextModelListener {
        void prepareInsert(@NonNull TextModel model, int line, int column, @NonNull String newText);
        
        void insertUpdate(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn);
        
        void prepareRemove(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn);
        
        void removeUpdate(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn);
    }
    
    void addTextModelListener(@NonNull TextModelListener listener);
    
    void removeTextModelListener(@NonNull TextModelListener listener);
    
    void setText(@NonNull String text);
    
    void insertLineBreak(int line, int column);
    
    void insert(int line, int column, char c);
    
    void insert(int line, int column, @NonNull String newText);
    
    void removeLineBreak(int line);
    
    void remove(int startLine, int startColumn, int endLine, int endColumn);
    
    void replace(int startLine, int startColumn, int endLine, int endColumn, @NonNull String newText);
    
    char getChar(int line, int column);
    
    @NonNull
    String getText(int startLine, int startColumn, int endLine, int endColumn);
    
    @NonNull
    String getText();
    
    @NonNull
    Reader getReader();
    
    @NonNull
    Reader getReader(int startLine, int startColumn, int endLine, int endColumn);
    
    @NonNull
    LineIterator getIterator(int startLine, int endLine);
    
    @NonNull
    LineIterator getIterator();
    
    @NonNull
    String getLineBreak();
    
    int getLineCount();
    
    int getLineLength(int line);
    
    @NonNull
    String getLineText(int line);
    
    void getLineChars(int line, int startColumn, int endColumn, @NonNull char[] dest, int offset);
    
    float measureLine(int line, int startColumn, int endColumn, @NonNull Paint paint);
    
    void getLineWidths(int line, int startColumn, int endColumn, @NonNull float[] widths, @NonNull Paint paint);
    
    void drawLine(int line, int startColumn, int endColumn, float x, float y, @NonNull Canvas canvas, @NonNull Paint paint);
    
    
    void setReadOnly(boolean readOnly);
    
    boolean isReadOnly();
    
    long lastEditTimestamps();
    
    void beginBatchEdit();
    
    void endBatchEdit();
    
    boolean isBatchEdit();
    
    boolean canUndo();
    
    boolean canRedo();
    
    void undo();
    
    void redo();
}

