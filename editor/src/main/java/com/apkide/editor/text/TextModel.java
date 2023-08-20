package com.apkide.editor.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

public interface TextModel extends TextGraphics, GetChars {
    
    void addTextChangeListener(@NonNull TextChangeListener listener);
    
    void removeTextChangeListener(@NonNull TextChangeListener listener);
    
    void setReadOnly(boolean readOnly);
    
    boolean isReadOnly();
    
    void setText(@NonNull String text);
    
    void insert(int offset, @NonNull String text);
    
    void delete(int start, int end);
    
    void replace(int start, int end, @NonNull String text);
    
    int getCharCount();
    
    char getChar(int offset);
    
    @NonNull
    String getText(int start, int end);
    
    @NonNull
    String getText();
    
    @Override
    void getChars(int start, int end, char[] dest, int destoff);
    
    @Override
    float measure(int start, int end, float monoAdvance, int tabSize, @NonNull Paint paint);
    
    @Override
    void getWidths(int start, int end, float monoAdvance, int tabSize, @NonNull Paint paint);
    
    @Override
    float draw(int start, int end, float x, float y, float monoAdvance, int tabSize,
               @NonNull Canvas canvas, @NonNull Paint paint);
    
    int getLineCount(@NonNull String text);
    
    int getLineCount(int start, int end);
    
    int getLineCount();
    
    int findLineNumber(int offset);
    
    int getLineStart(int line);
    
    int getColumnCount(int line, int tabSize);
    
    int getLineLength(int line);
    
    int getLineEnd(int line);
    
    @NonNull
    String getLineText(int line);
    
    @NonNull
    String getLineBreak(int line);
    
    @NonNull
    String getLineBreak();
    
    void beginCompoundEdit();
    
    void endCompoundEdit();
    
    boolean isCompoundEdit();
    
    boolean canUndo();
    
    boolean canRedo();
    
    void undo();
    
    void redo();
    
    
}
