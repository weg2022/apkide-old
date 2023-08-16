package com.apkide.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

public interface TextSource extends GetChars, CharSequence, TextGraphics {
    
    void set(@NonNull CharSequence text);
    
    void insert(int offset, @NonNull CharSequence text);
    
    void remove(int start, int end);
    
    void replace(int start, int end, @NonNull CharSequence text);
    
    @Override
    int length();
    
    @Override
    char charAt(int index);
    
    @NonNull
    String getText(int start, int end);
    
    @NonNull
    String getText();
    
    @NonNull
    @Override
    CharSequence subSequence(int start, int end);
    
    @NonNull
    @Override
    String toString();
    
    @Override
    float measure(int start, int end, @NonNull Paint paint);
    
    @Override
    void getWidths(int start, int end, @NonNull float[] widths, @NonNull Paint paint);
    
    @Override
    void draw(int start, int end, float x, float y, @NonNull Canvas canvas, @NonNull Paint paint);
}
