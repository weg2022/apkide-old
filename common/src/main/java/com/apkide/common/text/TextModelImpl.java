package com.apkide.common.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.apkide.common.io.IoUtils;
import com.apkide.common.io.iterator.LineIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class TextModelImpl implements TextModel {
    
    private final List<TextChangeListener> myListeners;
    private boolean myReadOnly;
    private final String myLineBreak;
    private final List<TextSource> myLines;
    private final TextSourceBuffer myBuffer;
    private int myEditLine;
    private long myLastEditTimestamps;
    
    public TextModelImpl() {
        myListeners = new Vector<>(1);
        myLines = new LinkedList<>();
        myLines.add(new TextSourceImpl());
        myLineBreak = System.lineSeparator();
        myBuffer = new TextSourceBuffer();
        myEditLine = -1;
    }
    
    @Override
    public void addTextModelListener(@NonNull TextChangeListener listener) {
        if (!myListeners.contains(listener))
            myListeners.remove(listener);
    }
    
    @Override
    public void removeTextModelListener(@NonNull TextChangeListener listener) {
        myListeners.remove(listener);
    }
    
    @Override
    public void setText(@NonNull String text) {
        remove(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        insert(0, 0, text);
    }
    
    @Override
    public void insertLineBreak(int line, int column) {
        
        for (TextChangeListener listener : myListeners) {
            listener.prepareInsert(this, line, column, getLineBreak());
        }
        
        synchronized (this) {
            switchEditLine(line);
            
            String deleted = myBuffer.getText(column, myBuffer.length());
            myLines.add(line + 1, new TextSourceBuffer(deleted));
            myBuffer.remove(column, myBuffer.length());
            
        }
        
        updateEditTimestamps();
        
        for (TextChangeListener listener : myListeners) {
            listener.insertUpdate(this, line, column, line + 1, -1);
        }
    }
    
    @Override
    public void insert(int line, int column, char c) {
        if (c == '\r' || c == '\n') {
            insertLineBreak(line, column);
            return;
        }
        
        for (TextChangeListener listener : myListeners) {
            listener.prepareInsert(this, line, column, String.valueOf(c));
        }
        
        synchronized (this) {
            switchEditLine(line);
            
            myBuffer.insert(column, String.valueOf(c));
            
            updateEditTimestamps();
        }
        
        for (TextChangeListener listener : myListeners) {
            listener.insertUpdate(this, line, column, line, column);
        }
    }
    
    @Override
    public void insert(int line, int column, @NonNull String newText) {
        for (TextChangeListener listener : myListeners) {
            listener.prepareInsert(this, line, column, newText);
        }
        int endLine = line;
        int endColumn = column;
        synchronized (this) {
            switchEditLine(line);
            BufferedReader reader = new BufferedReader(new StringReader(newText));
            try {
                String text;
                int workLine = line;
                while ((text = reader.readLine()) != null) {
                    if (workLine == line) {
                        myBuffer.insert(column, text);
                        endColumn = column + text.length() - 1;
                    } else {
                        myLines.add(workLine, new TextSourceBuffer(text));
                        endColumn = text.length() - 1;
                    }
                    
                    workLine++;
                }
                
                endLine = workLine - 1;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.safeClose(reader);
            }
            
            updateEditTimestamps();
        }
        
        for (TextChangeListener listener : myListeners) {
            listener.insertUpdate(this, line, column, endLine, endColumn);
        }
        
    }
    
    @Override
    public void removeLineBreak(int line) {
        switchEditLine(line);
        int column = myBuffer.length();
        
        for (TextChangeListener listener : myListeners) {
            listener.prepareRemove(this, line, column, line + 1, -1);
        }
        
        synchronized (this) {
            if (line + 1 < myLines.size())
                myBuffer.insert(myBuffer.length(), myLines.get(line + 1).getText());
            
            myLines.remove(line + 1);
            updateEditTimestamps();
        }
        
        for (TextChangeListener listener : myListeners) {
            listener.removeUpdate(this, line, column, line + 1, -1);
        }
    }
    
    @Override
    public void remove(int startLine, int startColumn, int endLine, int endColumn) {
        
        if (endLine >= getLineCount())
            endLine = getLineCount() - 1;
        
        if (endColumn > getLineLength(endLine))
            endColumn = getLineLength(endLine);
        
        for (TextChangeListener listener : myListeners) {
            listener.prepareRemove(this, startLine, startColumn, endLine, endColumn);
        }
        
        synchronized (this) {
            switchEditLine(startLine);
            if (startLine == endLine) {
                myBuffer.remove(startColumn, endColumn);
                
            } else {
                myBuffer.remove(startColumn, myBuffer.length());
                if (endLine > startLine + 1)
                    myLines.subList(startLine + 1, endLine).clear();
                
                if (startLine + 1 < myLines.size()) {
                    TextSource line = myLines.get(startLine + 1);
                    String text = line.getText(endColumn, line.length());
                    myBuffer.insert(myBuffer.length(), text);
                    myLines.remove(startLine + 1);
                }
            }
            
            updateEditTimestamps();
        }
        
        for (TextChangeListener listener : myListeners) {
            listener.removeUpdate(this, startLine, startColumn, endLine, endColumn);
        }
    }
    
    @Override
    public void replace(int startLine, int startColumn, int endLine, int endColumn,
                        @NonNull String newText) {
        remove(startLine, startColumn, endLine, endColumn);
        insert(startLine, startColumn, newText);
    }
    
    @Override
    public char getChar(int line, int column) {
        return getLine(line).charAt(column);
    }
    
    @NonNull
    @Override
    public String getText(int startLine, int startColumn, int endLine, int endColumn) {
        if (endLine >= getLineCount()) {
            endLine = getLineCount() - 1;
        }
        if (endColumn > getLineLength(endLine))
            endColumn = getLineLength(endLine);
        
        StringBuilder builder = new StringBuilder();
        if (startLine == endLine) {
            
            TextSource line = getLine(startLine);
            builder.append(line.getText(startColumn, endColumn));
            
            if (endColumn == line.length())
                builder.append(getLineBreak());
            
        } else {
            
            TextSource firstLine = getLine(startLine);
            builder.append(firstLine.getText(startColumn, firstLine.length())).
                    append(getLineBreak());
            
            for (int i = startLine; i < endLine; i++) {
                builder.append(getLine(i).getText()).append(getLineBreak());
            }
            
            TextSource lastLine = getLine(endLine);
            builder.append(lastLine.getText(0, endColumn));
            
            if (endLine != getLineCount() - 1 && endColumn == lastLine.length())
                builder.append(getLineBreak());
        }
        return builder.toString();
    }
    
    @NonNull
    @Override
    public String getText() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < getLineCount(); i++) {
            
            builder.append(getLine(i).getText());
            
            if (i != getLineCount() - 1)
                builder.append(getLineBreak());
        }
        return builder.toString();
    }
    
    @NonNull
    @Override
    public Reader getReader() {
        return getReader(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    @NonNull
    @Override
    public Reader getReader(int startLine, int startColumn, int endLine, int endColumn) {
        return new StringReader(getText(startLine, startColumn, endLine, endColumn));
    }
    
    @NonNull
    @Override
    public LineIterator getIterator(int startLine, int endLine) {
        return new LineIterator(getReader(startLine, 0, endLine, Integer.MAX_VALUE));
    }
    
    @NonNull
    @Override
    public LineIterator getIterator() {
        return new LineIterator(getReader());
    }
    
    @NonNull
    @Override
    public String getLineBreak() {
        return myLineBreak;
    }
    
    
    private void switchEditLine(int line) {
        if (myEditLine != -1)
            myLines.get(myEditLine).set(myBuffer);
        
        myEditLine = line;
        myBuffer.set(myLines.get(line));
    }
    
    protected TextSource getLine(int line) {
        if (myEditLine == line && myBuffer != null) {
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
        return getLine(line).length();
    }
    
    @NonNull
    @Override
    public String getLineText(int line) {
        return getLine(line).getText();
    }
    
    @Override
    public void getLineChars(int line, int startColumn, int endColumn,
                             @NonNull char[] dest, int offset) {
        getLine(line).getChars(startColumn, endColumn, dest, offset);
    }
    
    @Override
    public float measureLine(int line, int startColumn, int endColumn,
                             @NonNull Paint paint) {
        return getLine(line).measure(startColumn, endColumn, paint);
    }
    
    @Override
    public void getLineWidths(int line, int startColumn, int endColumn,
                              @NonNull float[] widths, @NonNull Paint paint) {
        getLine(line).getWidths(startColumn, endColumn, widths, paint);
    }
    
    @Override
    public void drawLine(int line, int startColumn, int endColumn, float x, float y,
                         @NonNull Canvas canvas, @NonNull Paint paint) {
        getLine(line).draw(startColumn, endColumn, x, y, canvas, paint);
    }
    
    @Override
    public void setReadOnly(boolean readOnly) {
        myReadOnly = readOnly;
    }
    
    @Override
    public boolean isReadOnly() {
        return myReadOnly;
    }
    
    synchronized protected void updateEditTimestamps() {
        long timestamps = System.currentTimeMillis();
        if (timestamps == myLastEditTimestamps)
            myLastEditTimestamps++;
        else
            myLastEditTimestamps = timestamps;
    }
    
    @Override
    synchronized public long lastEditTimestamps() {
        return myLastEditTimestamps;
    }
    
    @Override
    public void beginBatchEdit() {
    
    }
    
    @Override
    public void endBatchEdit() {
    
    }
    
    @Override
    public boolean isBatchEdit() {
        return false;
    }
    
    @Override
    public boolean canUndo() {
        return false;
    }
    
    @Override
    public boolean canRedo() {
        return false;
    }
    
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @NonNull
    @Override
    public TextModel clone() {
        TextModelImpl model = new TextModelImpl();
        model.myLines.clear();
        model.myLines.addAll(myLines);
        model.myReadOnly = myReadOnly;
        model.myBuffer.set(myBuffer);
        model.myEditLine = myEditLine;
        model.myLastEditTimestamps = myLastEditTimestamps;
        return model;
    }
}
