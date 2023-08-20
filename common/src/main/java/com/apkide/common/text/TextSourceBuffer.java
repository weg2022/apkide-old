package com.apkide.common.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

 class TextSourceBuffer implements TextSource {
    private static final Object sLock = new Object();
    
    private static char[] sTemp = null;
    
    private static char[] obtain(int len) {
        char[] buf;
        
        synchronized (sLock) {
            buf = sTemp;
            sTemp = null;
        }
        
        if (buf == null || buf.length < len)
            buf = new char[len];
        return buf;
    }
    
    private static void recycle(char[] temp) {
        if (temp.length > 1000)
            return;
        
        synchronized (sLock) {
            sTemp = temp;
        }
    }
    
    
    private final StringBuilder myBuffer = new StringBuilder();
    
    public TextSourceBuffer() {
    
    }
    
    public TextSourceBuffer(@NonNull CharSequence text) {
        myBuffer.append(text);
    }
    
    @Override
    public void set(@NonNull CharSequence text) {
        myBuffer.delete(0, myBuffer.length());
        myBuffer.insert(0, text);
        //myBuffer.replace(0,myBuffer.length(),text);
    }
    
    @Override
    public void insert(int offset, @NonNull CharSequence text) {
        myBuffer.insert(offset, text);
    }
    
    @Override
    public void remove(int start, int end) {
        myBuffer.delete(start, end);
    }
    
    @Override
    public void replace(int start, int end, @NonNull CharSequence text) {
        myBuffer.delete(start, end);
        myBuffer.insert(start, text);
        // myBuffer.replace(start, end, text);
    }
    
    @Override
    public int length() {
        return myBuffer.length();
    }
    
    @Override
    public char charAt(int index) {
        return myBuffer.charAt(index);
    }
    
    @NonNull
    @Override
    public String getText(int start, int end) {
        char[] chars = new char[end - start];
        myBuffer.getChars(start, end, chars, 0);
        return String.valueOf(chars);
    }
    
    @NonNull
    @Override
    public String getText() {
        return myBuffer.toString();
    }
    
    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return myBuffer.subSequence(start, end);
    }
    
    @NonNull
    @Override
    public String toString() {
        return myBuffer.toString();
    }
    
    //TODO: Performance optimization
    @Override
    public float measure(int start, int end, @NonNull Paint paint) {
        char[] chars = obtain(end - start);
        myBuffer.getChars(start, end, chars, 0);
        float r = paint.measureText(myBuffer, start, end);
        recycle(chars);
        return r;
    }
    
    //TODO: Performance optimization
    @Override
    public void getWidths(int start, int end, @NonNull float[] widths, @NonNull Paint paint) {
        char[] chars = obtain(end - start);
        myBuffer.getChars(start, end, chars, 0);
        paint.getTextWidths(myBuffer, start, end, widths);
        recycle(chars);
    }
    
    //TODO: Performance optimization
    @Override
    public void draw(int start, int end, float x, float y, @NonNull Canvas canvas, @NonNull Paint paint) {
        char[] chars = obtain(end - start);
        myBuffer.getChars(start, end, chars, 0);
        canvas.drawText(chars, 0, end - start, x, y, paint);
        recycle(chars);
    }
    
    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        myBuffer.getChars(start, end, dest, destoff);
    }
}
