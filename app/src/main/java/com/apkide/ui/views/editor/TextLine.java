package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

public interface TextLine extends TextGraphics {
    void set(@NonNull char[] chars);

    void insert(int index, @NonNull char[] chars);

    void delete(int start, int end);

    char get(int index);

    int getOffset();

    int getLength();

    @NonNull
    String getDelimiter();

    @NonNull
    char[] getArray();

    @NonNull
    String get();

    void copyChars(int start, int end, char[] dest, int destoff);

    @Override
    float nativeMeasureText(int start, int end, Paint paint);

    @Override
    int nativeGetTextWidths(int start, int end, float[] widths, Paint paint);

    @Override
    void nativeDrawText(Canvas canvas, int start, int end, float x, float y, Paint paint);
}
