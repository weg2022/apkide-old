package com.apkide.ui.views.editor;

import static java.lang.System.arraycopy;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

public class TextLineImpl implements TextLine, TextLineExtension {

    private char[] myChars = new char[10];
    private int myLength = 0;

    private int offset = -1;
    private String delimiter = "";

    @Override
    public void set(@NonNull char[] chars) {
        myChars = new char[chars.length * 5 / 4];

        if (chars.length != 0)
            arraycopy(chars, 0, myChars, 0, chars.length);

        myLength = chars.length;
    }

    private void resize(int minLength) {
        if (myChars.length <= (myLength + minLength)) {
            char[] temp = new char[(myLength + minLength) * 5 / 4];
            arraycopy(myChars, 0, temp, 0, myChars.length);
            myChars = temp;
        }
    }

    @Override
    public void insert(int index, @NonNull char[] chars) {
        resize(chars.length);
        arraycopy(chars, 0, myChars, index, chars.length);
        myLength += chars.length;
    }

    @Override
    public void delete(int start, int end) {
        arraycopy(myChars, end + 1, myChars, start, myLength - (end + 1));
        myLength -= end - start;
    }

    @Override
    public char get(int index) {
        return myChars[index];
    }

    @Override
    public int getLength() {
        return myLength;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void setDelimiter(@NonNull String delimiter) {
        this.delimiter = delimiter;
    }

    @NonNull
    @Override
    public String getDelimiter() {
        return delimiter;
    }

    @NonNull
    @Override
    public char[] getArray() {
        char[] chars = new char[myLength];
        arraycopy(myChars, 0, chars, 0, myLength);
        return chars;
    }

    @NonNull
    @Override
    public String get() {
        return new String(myChars, 0, myLength);
    }

    @Override
    public void copyChars(int start, int end, char[] dest, int destoff) {
        arraycopy(myChars, start, dest, destoff, end - start);
    }

    @Override
    public float nativeMeasureText(int start, int end, Paint paint) {
        return paint.measureText(myChars, start, end - start);
    }

    @Override
    public int nativeGetTextWidths(int start, int end, float[] widths, Paint paint) {
        return paint.getTextWidths(myChars, start, end - start, widths);
    }

    @Override
    public void nativeDrawText(Canvas canvas, int start, int end, float x, float y, Paint paint) {
        canvas.drawText(myChars, start, end - start, x, y, paint);
    }


}
