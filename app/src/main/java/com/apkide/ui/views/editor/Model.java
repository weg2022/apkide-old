package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Reader;

public interface Model {

    void addModelListener(@NonNull ModelListener listener);

    void removeModelListener(@NonNull ModelListener listener);

    void insertChar(int line, int column, char c);

    void insertChars(int line, int column, @NonNull char[] chars);

    void insertChars(int line, int column, @NonNull CharSequence text);

    void insertLineBreak(int line, int column);

    void removeLineBreak(int line);

    void removeChar(int line, int column);

    void removeChars(int line, int column, int length);

    void removeChars(int startLine, int startColumn, int endLine, int endColumn);

    char getChar(int line, int column);

    int getLineCount();

    int getLineLength(int line);

    void getLineChars(int line, int column, int length, @NonNull char[] dest, int destoff);

    @NonNull
    char[] getLineChars(int line);

    @NonNull
    char[] getLineChars(int line, int column, int length);

    @NonNull
    CharSequence getLineSequence(int line);

    void drawLine(Canvas canvas, int line, int column, int length, float x, float y, @NonNull Paint paint);

    float measureLine(int line, int column, int length, @NonNull Paint paint);

    void getLineWidths(int line, int column, int length, float[] widths, @NonNull Paint paint);

    @NonNull
    String getLineBreak();

    long getLastEditTimestamps();

    boolean isReadOnly();
    @NonNull
    Reader getReader();

    @NonNull
    Reader getReader(int startLine, int startColumn, int endLine, int endColumn);


    int getStyle(int line, int column);

    int getStyleCount();

    @Nullable
    TextStyle getTextStyle(int style);

    boolean isColor(int line, int column);

    int getColor(int line, int column);

    boolean isWarning(int line, int column);

    boolean isError(int line, int column);
}
