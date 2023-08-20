package com.apkide.common.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

 class TextSourceImpl implements TextSource {
    private static final char[] EMPTY = new char[0];
    private char[] myChars;
    
    public TextSourceImpl() {
        myChars = EMPTY;
    }
    
    public TextSourceImpl(@NonNull char[] chars) {
        myChars = chars;
    }
    
    public TextSourceImpl(@NonNull CharSequence sequence) {
        if (sequence instanceof TextSourceImpl) {
            myChars = ((TextSourceImpl) sequence).myChars;
            return;
        }
        
        if (sequence instanceof GetChars) {
            myChars = new char[sequence.length()];
            ((GetChars) sequence).getChars(0, sequence.length(), myChars, 0);
            return;
        }
        
        myChars = new char[sequence.length()];
        for (int i = 0; i < sequence.length(); i++) {
            myChars[i] = sequence.charAt(i);
        }
    }
    
    @Override
    public void set(@NonNull CharSequence text) {
        if (text instanceof TextSourceImpl) {
            myChars = ((TextSourceImpl) text).myChars;
            return;
        }
        
        if (text instanceof GetChars) {
            myChars = new char[text.length()];
            ((GetChars) text).getChars(0, text.length(), myChars, 0);
            return;
        }
        
        myChars = new char[text.length()];
        for (int i = 0; i < text.length(); i++) {
            myChars[i] = text.charAt(i);
        }
    }
    
    @Override
    public void insert(int offset, @NonNull CharSequence text) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void remove(int start, int end) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void replace(int start, int end, @NonNull CharSequence text) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int length() {
        return myChars.length;
    }
    
    @Override
    public char charAt(int index) {
        return myChars[index];
    }
    
    @NonNull
    @Override
    public String getText(int start, int end) {
        return String.valueOf(myChars, start, end - start);
    }
    
    @NonNull
    @Override
    public String getText() {
        return String.valueOf(myChars);
    }
    
    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return String.valueOf(myChars, start, end - start);
    }
    
    @NonNull
    @Override
    public String toString() {
        return String.valueOf(myChars);
    }
    
    @Override
    public float measure(int start, int end, @NonNull Paint paint) {
        return paint.measureText(myChars, start, end - start);
    }
    
    @Override
    public void getWidths(int start, int end, @NonNull float[] widths, @NonNull Paint paint) {
        paint.getTextWidths(myChars, start, end - start, widths);
    }
    
    @Override
    public void draw(int start, int end, float x, float y, @NonNull Canvas canvas, @NonNull Paint paint) {
        canvas.drawText(myChars, start, end - start, x, y, paint);
    }
    
    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        System.arraycopy(myChars, start, dest, destoff, end - start);
    }
}
