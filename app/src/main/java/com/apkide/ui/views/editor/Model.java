package com.apkide.ui.views.editor;

import static java.lang.System.arraycopy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.GetChars;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Model implements CharSequence, GetChars, TextGraphics {

    private final List<ModelListener> myListeners = new ArrayList<>(1);
    private char[] myChars;
    private int myGapStart;
    private int myGapLength;
    private final LineManger myLineManger;
    private final SpanStore mySpanStore;

    public Model() {
        myLineManger = new LineManger();
        mySpanStore = new SpanStore();
    }

    public void addModelListener(@NonNull ModelListener listener) {
        if (!myListeners.contains(listener))
            myListeners.add(listener);
    }

    public void removeModelListener(@NonNull ModelListener listener) {
        myListeners.remove(listener);
    }

    public void setText(@NonNull CharSequence text) {
        moveGap(0, text.length());
        myGapStart = text.length();
        myGapLength = myChars.length - text.length();
        myLineManger.set(text);
        textSet();
    }

    public void insert(int offset, @NonNull CharSequence text) {
        int len = text.length();
        if (len == 0) return;

        int startLine = getLineNumber(offset);
        int startColumn = getColumnNumber(startLine, offset);
        int endLine = getLineNumber(offset + len);
        int endColumn = getColumnNumber(endLine, offset + len);
        textPreInsert(startLine, startColumn, endLine, endColumn, text);

        moveGap(offset, len);
        if (text instanceof GetChars)
            ((GetChars) text).getChars(0, len, myChars, myGapStart);
        else {
            for (int i = 0; i < len; i++) {
                myChars[myGapStart + i] = text.charAt(i);
            }
        }
        myGapStart += len;
        myGapLength -= len;
        myLineManger.replace(offset, 0, text);

        textInserted(startLine, startColumn, endLine, endColumn, text);
    }

    public void delete(int start, int end) {
        int len = end - start;

        int startLine = getLineNumber(start);
        int startColumn = getColumnNumber(startLine, start);
        int endLine = getLineNumber(end);
        int endColumn = getColumnNumber(endLine, end);

        textPreRemove(startLine, startColumn, endLine, endColumn);

        moveGap(start, 0);
        myGapLength += start;
        myLineManger.replace(start, len, "");

        textRemoved(startLine, startColumn, endLine, endColumn);
    }


    protected void textSet() {
        mySpanStore.reset();
        for (ModelListener listener : myListeners) {
            listener.textSet(this);
        }
    }


    protected void textPreInsert(int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence text) {
        mySpanStore.adjustSpanOnInserted(startLine, startColumn, endLine, endColumn);
        for (ModelListener listener : myListeners) {
            listener.textPreInsert(this, startLine, startColumn, endLine, endColumn, text);
        }
    }

    protected void textInserted(int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence text) {
        for (ModelListener listener : myListeners) {
            listener.textInserted(this, startLine, startColumn, endLine, endColumn, text);
        }
    }

    protected void textPreRemove(int startLine, int startColumn, int endLine, int endColumn) {
        mySpanStore.adjustSpanOnRemoved(startLine, startColumn, endLine, endColumn);
        for (ModelListener listener : myListeners) {
            listener.textPreRemove(this, startLine, startColumn, endLine, endColumn);
        }
    }

    protected void textRemoved(int startLine, int startColumn, int endLine, int endColumn) {
        for (ModelListener listener : myListeners) {
            listener.textRemoved(this, startLine, startColumn, endLine, endColumn);
        }
    }


    @NonNull
    public String getTextRange(int start, int end) {
        int len = end - start;
        char[] buf = new char[len];
        getChars(start, end, buf, 0);
        return new String(buf);
    }

    @NonNull
    public String getText() {
        int len = length();
        char[] buf = new char[len];
        getChars(0, len, buf, 0);
        return new String(buf);
    }

    @NonNull
    public char[] getTextArrayRange(int start, int end) {
        int len = end - start;
        char[] buf = new char[len];
        getChars(start, end, buf, 0);
        return buf;
    }

    @NonNull
    public char[] getTextArray() {
        int len = length();
        char[] buf = new char[len];
        getChars(0, len, buf, 0);
        return buf;
    }

    @Override
    public int length() {
        return myChars.length - myGapLength;
    }

    @Override
    public char charAt(int offset) {
        return myChars[offset < myGapStart ? offset : offset + myGapLength];
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        int len = end - start;
        char[] buf = new char[len];
        getChars(start, end, buf, 0);
        return new String(buf);
    }

    @NonNull
    @Override
    public String toString() {
        int len = length();
        char[] buf = new char[len];
        getChars(0, len, buf, 0);
        return new String(buf);
    }

    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        if (end <= myGapStart) {
            arraycopy(myChars, start, dest, destoff, end - start);
        } else if (start >= myGapStart) {
            arraycopy(myChars, start + myGapLength, dest, destoff, end - start);
        } else {
            arraycopy(myChars, start, dest, destoff, myGapStart - start);
            arraycopy(myChars, myGapStart + myGapLength,
                    dest, destoff + (myGapStart - start),
                    end - myGapStart);
        }
    }

    private void moveGap(int newGapStart, int minGapLength) {
        char[] buffer;
        int newGapLength;
        if (myGapLength >= minGapLength) {
            buffer = myChars;
            newGapLength = myGapLength;
        } else {
            int textLength = myChars.length - myGapLength;
            int newSize = (textLength * 5) / 4;
            buffer = new char[Math.max(newSize, textLength + minGapLength)];
            newGapLength = buffer.length - textLength;
        }

        int gapEnd = myGapStart + myGapLength;
        int newGapEnd = newGapStart + newGapLength;
        if (newGapStart < myGapStart) {
            arraycopy(myChars, 0, buffer, 0, newGapStart);
            arraycopy(myChars, newGapStart, buffer, newGapEnd, myGapStart - newGapStart);
            arraycopy(myChars, gapEnd, buffer, newGapEnd + (myGapStart - newGapStart), myChars.length - gapEnd);
        } else {
            arraycopy(myChars, 0, buffer, 0, myGapStart);
            arraycopy(myChars, gapEnd, buffer, myGapStart, newGapStart - myGapStart);
            arraycopy(myChars, gapEnd + (newGapStart - myGapStart), buffer, newGapEnd, buffer.length - newGapEnd);
        }
        myChars = buffer;
        myGapStart = newGapStart;
        myGapLength = newGapLength;
    }

    public int getLineCount() {
        return myLineManger.getLineCount();
    }

    public int getLineNumber(int offset) {
        return myLineManger.findLineNumber(offset);
    }

    public int getColumnNumber(int offset) {
        return getColumnNumber(getLineNumber(offset), offset);
    }

    private int getColumnNumber(int line, int offset) {
        int start = getLineStart(line);
        return offset - start;
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

    public LineInformation getLineInformation(int line){
        return myLineManger.getLineInformation(line);
    }

    public int computeLineCount(@NonNull CharSequence text){
        return myLineManger.lineCount(text);
    }

    public int computeLineCount(int start,int end){
        return myLineManger.getLineCount(start,end-start);
    }
    @Override
    public float nativeMeasureText(int start, int end, Paint paint) {
        float ret;

        if (end <= myGapStart) {
            ret = paint.measureText(myChars, start, end - start);
        } else if (start >= myGapStart) {
            ret = paint.measureText(myChars, start + myGapLength, end - start);
        } else {
            char[] buf = obtain(end - start);
            getChars(start, end, buf, 0);
            ret = paint.measureText(buf, 0, end - start);
            recycle(buf);
        }

        return ret;
    }

    @Override
    public int nativeGetTextWidths(int start, int end, float[] widths, Paint paint) {
        int ret;

        if (end <= myGapStart) {
            ret = paint.getTextWidths(myChars, start, end - start, widths);
        } else if (start >= myGapStart) {
            ret = paint.getTextWidths(myChars, start + myGapLength, end - start, widths);
        } else {
            char[] buf = obtain(end - start);
            getChars(start, end, buf, 0);
            ret = paint.getTextWidths(buf, 0, end - start, widths);
            recycle(buf);
        }

        return ret;
    }

    @Override
    public void nativeDrawText(Canvas canvas, int start, int end, float x, float y, Paint paint) {

        if (end <= myGapStart) {
            canvas.drawText(myChars, start, end - start, x, y, paint);
        } else if (start >= myGapStart) {
            canvas.drawText(myChars, start + myGapLength, end - start, x, y, paint);
        } else {
            char[] buf = obtain(end - start);
            getChars(start, end, buf, 0);
            canvas.drawText(buf, 0, end - start, x, y, paint);
            recycle(buf);
        }

    }

    private static char[] sTemp = null;
    private static final Object sLock = new Object();

    static char[] obtain(int len) {
        char[] buf;

        synchronized (sLock) {
            buf = sTemp;
            sTemp = null;
        }

        if (buf == null || buf.length < len)
            buf = new char[len];

        return buf;
    }

    static void recycle(char[] temp) {
        if (temp.length > 1000)
            return;

        synchronized (sLock) {
            sTemp = temp;
        }
    }

}
