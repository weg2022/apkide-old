package com.apkide.common.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

public interface TextGraphics {
    
    float measure(int start, int end, @NonNull Paint paint);
    
    void getWidths(int start, int end, @NonNull float[] widths, @NonNull Paint paint);
    
    void draw(int start, int end, float x, float y, @NonNull Canvas canvas, @NonNull Paint paint);
}
