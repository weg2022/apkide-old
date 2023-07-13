package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

public class Model implements CharSequence, GetChars,TextGraphics {

    private final LineManger myLineManger;
    private SpanStore mySpanStore;

    public Model() {
        myLineManger = new LineManger();
        mySpanStore = new SpanStore();
    }


    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int offset) {
        return 0;
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }


    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {

    }

    public int getLineCount() {
        return myLineManger.getLineCount();
    }

    public int getLineNumber(int offset) {
        return myLineManger.findLineNumber(offset);
    }

    public int getLineStart(int line) {
        return myLineManger.getLineStart(line);
    }

    public int getLineLength(int line) {
        return myLineManger.getLineLength(line);
    }

    public int getFullLineLength(int line) {
        return myLineManger.getFullLineLength(line);
    }

    public String getLineDelimiter(int line) {
        return myLineManger.getLineDelimiter(line);
    }

    private void checkLine(int line) {
        if (line < 0 || line >= getLineCount())
            throw new IllegalArgumentException("Invalid line index: " + line);
    }

    private void checkLine(int line, int column) {
        checkLine(line);
        int len = getFullLineLength(line);
        if (column < 0 || column >= len)
            throw new IllegalArgumentException("Invalid column: " + column);
    }

    @Override
    public float nativeMeasureText(int start, int end, Paint paint) {
        return 0;
    }

    @Override
    public int nativeGetTextWidths(int start, int end, float[] widths, Paint paint) {
        return 0;
    }

    @Override
    public void nativeDrawText(Canvas canvas, int start, int end, float x, float y, Paint paint) {

    }
}
