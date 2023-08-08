package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

import androidx.annotation.NonNull;

public class TextLineImpl implements TextLine {

    private static final char[] EMPTY = new char[0];
    private char[] myChars = EMPTY;

    public TextLineImpl() {}

    public TextLineImpl(char[] chars) {
        myChars = chars;
    }

    public TextLineImpl(CharSequence text) {
        myChars = new char[text.length()];
        TextUtils.getChars(text, 0, text.length(), myChars, 0);
    }


    @Override
    public void set(@NonNull char[] chars) {
        myChars = chars;
    }

    @Override
    public void set(@NonNull CharSequence text) {
        myChars = new char[text.length()];
        TextUtils.getChars(text, 0, text.length(), myChars, 0);
    }

    @Override
    public void append(@NonNull CharSequence text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void append(@NonNull char[] chars) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int index, @NonNull CharSequence text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int index, @NonNull char[] chars) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int start, int end) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public char[] getArray() {
        return myChars;
    }

    @NonNull
    @Override
    public String get() {
        return String.valueOf(myChars);
    }

    @NonNull
    @Override
    public String get(int start, int end) {
        return String.valueOf(myChars, start, end - start);
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
    public void draw(@NonNull Canvas canvas, int start, int end, float x, float y, @NonNull Paint paint) {
        canvas.drawText(myChars, start, end - start, x, y, paint);
    }

    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        System.arraycopy(myChars, start, dest, destoff, end - start);
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
    public CharSequence subSequence(int start, int end) {
        return String.valueOf(myChars, start, end - start);
    }
}
