package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;

public class TextBuffer implements TextLine {
    private static final int LIMIT = 2048;

    private final SpannableStringBuilder myBuilder = new SpannableStringBuilder();

    public TextBuffer(char[] chars) {
        this(String.valueOf(chars));
    }

    public TextBuffer(CharSequence text) {
        myBuilder.clear();
        myBuilder.append(text);
    }

    @Override
    public void set(@NonNull char[] chars) {
        set(String.valueOf(chars));
    }

    @Override
    public void set(@NonNull CharSequence text) {
        myBuilder.clear();
        myBuilder.append(text);
    }

    @Override
    public void append(@NonNull CharSequence text) {
        myBuilder.append(text);
    }

    @Override
    public void append(@NonNull char[] chars) {
        append(String.valueOf(chars));
    }

    @Override
    public void insert(int index, @NonNull CharSequence text) {
        myBuilder.insert(index, text);
    }

    @Override
    public void insert(int index, @NonNull char[] chars) {
        insert(index, String.valueOf(chars));
    }

    @Override
    public void delete(int start, int end) {
        myBuilder.delete(start, end);
    }

    @NonNull
    @Override
    public char[] getArray() {
        return myBuilder.toString().toCharArray();
    }

    @NonNull
    @Override
    public String get() {
        return myBuilder.toString();
    }

    @NonNull
    @Override
    public String get(int start, int end) {
        return myBuilder.substring(start, end);
    }

    @Override
    public float measure(int start, int end, @NonNull Paint paint) {
        //  return myBuilder.measureText(start,end,paint);
        if (length() < LIMIT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return paint.measureText(myBuilder, start, end);
        }
        char[] temp = CharBuffer.obtain(end - start);
        getChars(start, end, temp, 0);
        float w = paint.measureText(temp, 0, end - start);
        CharBuffer.recycle(temp);
        return w;
    }

    @Override
    public int getWidths(int start, int end, float[] widths, @NonNull Paint paint) {
        // return myBuilder.getTextWidths(start,end,widths,paint);
        if (length() < LIMIT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return paint.getTextWidths(myBuilder, start, end, widths);
        }
        char[] temp = CharBuffer.obtain(end - start);
        getChars(start, end, temp, 0);
        int r = paint.getTextWidths(temp, 0, end - start, widths);
        CharBuffer.recycle(temp);
        return r;
    }

    @Override
    public void draw(@NonNull Canvas canvas, int start, int end, float x, float y, @NonNull Paint paint) {
        // myBuilder.drawText(canvas,start,end,x,y,paint);
        if (length() < LIMIT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            canvas.drawText(myBuilder, start, end, x, y, paint);
            return;
        }
        char[] temp = CharBuffer.obtain(end - start);
        getChars(start, end, temp, 0);
        canvas.drawText(temp, 0, end - start, x, y, paint);
        CharBuffer.recycle(temp);
    }

    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        myBuilder.getChars(start, end, dest, destoff);
    }

    @Override
    public int length() {
        return myBuilder.length();
    }

    @Override
    public char charAt(int index) {
        return myBuilder.charAt(index);
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return myBuilder.subSequence(start, end);
    }
}
