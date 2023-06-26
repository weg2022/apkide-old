package com.apkide.common.text;

import android.graphics.Canvas;
import android.graphics.Paint;


public interface TextGraphics {

    void nativeDrawText(Canvas canvas, int offset, int length, float x, float y, Paint paint);
/*
    void nativeDrawTextRun(Canvas canvas, int offset, int length, int contextOffset, int contextLength,
                           float x, float y, boolean isRtl, Paint paint);*/

    int nativeGetTextWidths(int offset, int length, float[] widths, Paint paint);

    float nativeMeasureText(int offset, int length, Paint paint);
}
