package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface TextGraphics {

    float nativeMeasureText(int start, int end, Paint paint);

    int nativeGetTextWidths(int start, int end, float[] widths, Paint paint);

    void nativeDrawText(Canvas canvas, int start, int end, float x, float y, Paint paint);
}
