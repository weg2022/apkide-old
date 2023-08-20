package com.apkide.editor.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

public interface TextGraphics {
    
    float measure(int start, int end, float monoAdvance, int tabSize,@NonNull Paint paint);
    
    void getWidths(int start, int end, float monoAdvance, int tabSize,@NonNull Paint paint);
    
    float draw(int start, int end, float x, float y, float monoAdvance, int tabSize, @NonNull Canvas canvas, @NonNull Paint paint);
}
