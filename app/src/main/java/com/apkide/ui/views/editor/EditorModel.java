package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EditorModel implements Model {
    private final List<ModelListener> myListeners;
    private final List<TextLine> myLines;
    private TextBuffer myBuffer;
    private int mySpanLine;
    private final String myLineBreak;
    private long myLastTimestamps;

    public EditorModel() {
        myListeners = new Vector<>(1);
        myLines = new ArrayList<>(100);//use LinkedList ?
        myLines.add(new TextLineImpl());
        myLineBreak = System.lineSeparator();
        myLastTimestamps = System.currentTimeMillis();
        mySpanLine = -1;
        myBuffer = null;
        switchSpanLine(0);
    }

    @Override
    public void addModelListener(@NonNull ModelListener listener) {
        if (!myListeners.contains(listener))
            myListeners.add(listener);
    }

    @Override
    public void removeModelListener(@NonNull ModelListener listener) {
        myListeners.remove(listener);
    }

    @Override
    public void insertChar(int line, int column, char c) {
        synchronized (this) {
            switchSpanLine(line);
            myBuffer.insert(column, String.valueOf(c));
            lastEditTimestamps();
        }
        fireInsertUpdate(line, column, line, column);
    }

    @Override
    public void insertChars(int line, int column, @NonNull char[] chars) {
        synchronized (this) {
            switchSpanLine(line);
            myBuffer.insert(column, chars);
            lastEditTimestamps();
        }
        fireInsertUpdate(line, column, line, (column + chars.length) - 1);
    }

    @Override
    public void insertChars(int line, int column, @NonNull CharSequence text) {
        synchronized (this) {
            switchSpanLine(line);
            myBuffer.insert(column, text);
            lastEditTimestamps();
        }
        fireInsertUpdate(line, column, line, (column + text.length()) - 1);
    }

    @Override
    public void insertLineBreak(int line, int column) {
        synchronized (this) {
            switchSpanLine(line);
            String deleted = myBuffer.get(column, myBuffer.length());
            myLines.add(line + 1, new TextLineImpl(deleted));
            myBuffer.delete(column, myBuffer.length());
            lastEditTimestamps();
        }
        fireInsertUpdate(line, column, line + 1, -1);
    }

    protected void fireInsertUpdate(int startLine, int startColumn, int endLine, int endColumn) {
        for (ModelListener listener : myListeners) {
            listener.insertUpdate(this, startLine, startColumn, endLine, endColumn);
        }
    }

    @Override
    public void removeLineBreak(int line) {
        int column;
        synchronized (this) {
            switchSpanLine(line);
            column = myBuffer.length();
            myBuffer.append(myLines.get(line + 1));
            myLines.remove(line + 1);
            lastEditTimestamps();
        }
        fireRemoveUpdate(line, column, line + 1, -1);
    }

    @Override
    public void removeChar(int line, int column) {
        synchronized (this) {
            switchSpanLine(line);
            myBuffer.delete(column - 1, column);
            lastEditTimestamps();
        }
        fireRemoveUpdate(line, column, line, column);
    }

    @Override
    public void removeChars(int line, int column, int length) {
        synchronized (this) {
            switchSpanLine(line);
            myBuffer.delete(column, column + length);
            lastEditTimestamps();
        }
        fireRemoveUpdate(line, column, line, (column + length) - 1);
    }

    @Override
    public void removeChars(int startLine, int startColumn, int endLine, int endColumn) {
        if (startLine > endLine || (startLine == endLine && endColumn < startColumn)) return;

        if (endLine >= getLineCount())
            endLine = getLineCount() - 1;

        if (endColumn >= getLineLength(endLine))
            endColumn = getLineLength(endLine);
        synchronized (this) {
            if (startLine == endLine) {
                switchSpanLine(startLine);
                myBuffer.delete(startColumn, endColumn);
            } else {
                switchSpanLine(startLine);
                myBuffer.delete(startColumn, myBuffer.length());
                if (endLine > startLine + 1)
                    myLines.subList(startLine + 1, endLine).clear();

                TextLine textLine = myLines.get(startLine + 1);
                String oldText = textLine.get(endColumn, textLine.length());
                myBuffer.append(oldText);
                myLines.remove(startLine + 1);
            }

            lastEditTimestamps();
        }

        fireRemoveUpdate(startLine, startColumn, endLine, endColumn);
    }

    protected void fireRemoveUpdate(int startLine, int startColumn, int endLine, int endColumn) {
        for (ModelListener listener : myListeners) {
            listener.removeUpdate(this, startLine, startColumn, endLine, endColumn);
        }
    }

    @Override
    public char getChar(int line, int column) {
        return lineAt(line).charAt(column);
    }


    private void switchSpanLine(int line) {
        if (mySpanLine != line && mySpanLine >= 0 && mySpanLine < myLines.size() && myBuffer != null)
            myLines.set(mySpanLine, new TextLineImpl(myBuffer));

        mySpanLine = line;
        if (myBuffer == null)
            myBuffer = new TextBuffer(myLines.get(line));
        else
            myBuffer.set(myLines.get(line));
    }

    private TextLine lineAt(int line) {
        if (mySpanLine == line && myBuffer != null) {
            return myBuffer;
        }
        return myLines.get(line);
    }

    @Override
    public int getLineCount() {
        return myLines.size();
    }

    @Override
    public int getLineLength(int line) {
        return lineAt(line).length();
    }

    @Override
    public void getLineChars(int line, int column, int length, @NonNull char[] dest, int destoff) {
        lineAt(line).getChars(column, column + length, dest, destoff);
    }

    @NonNull
    @Override
    public char[] getLineChars(int line) {
        return lineAt(line).getArray();
    }

    @NonNull
    @Override
    public char[] getLineChars(int line, int column, int length) {
        return lineAt(line).get(column, column + length).toCharArray();
    }

    @NonNull
    @Override
    public CharSequence getLineSequence(int line) {
        return lineAt(line);
    }

    @Override
    public void drawLine(Canvas canvas, int line, int column, int length, float x, float y, @NonNull Paint paint) {
        lineAt(line).draw(canvas, column, column + length, x, y, paint);
    }

    @Override
    public float measureLine(int line, int column, int length, @NonNull Paint paint) {
        return lineAt(line).measure(column, column + length, paint);
    }

    @Override
    public void getLineWidths(int line, int column, int length, float[] widths, @NonNull Paint paint) {
        lineAt(line).getWidths(column, column + length, widths, paint);
    }

    @NonNull
    @Override
    public String getLineBreak() {
        return myLineBreak;
    }

    @Override
    public long getLastEditTimestamps() {
        return myLastTimestamps;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    private synchronized void lastEditTimestamps() {
        long timestamps = System.currentTimeMillis();
        if (timestamps <= myLastTimestamps) {
            myLastTimestamps++;
        } else {
            myLastTimestamps = timestamps;
        }
    }

    @NonNull
    @Override
    public Reader getReader() {
        return getReader(0, 0, Integer.MAX_VALUE, 0);
    }

    @NonNull
    @Override
    public Reader getReader(int startLine, int startColumn, int endLine, int endColumn) {
        return new ModelReader(this, startLine, startColumn, endLine, endColumn, false, getLineBreak());
    }

    @Override
    public int getStyle(int line, int column) {
        return 0;
    }

    @Override
    public int getStyleCount() {
        return 0;
    }

    @Nullable
    @Override
    public TextStyle getTextStyle(int style) {
        return null;
    }

    @Override
    public boolean isColor(int line, int column) {
        return false;
    }

    @Override
    public int getColor(int line, int column) {
        return 0;
    }

    @Override
    public boolean isWarning(int line, int column) {
        return false;
    }

    @Override
    public boolean isError(int line, int column) {
        return false;
    }
}
