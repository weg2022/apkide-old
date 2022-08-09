package com.apkide.common;

import static java.lang.System.arraycopy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.GetChars;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class Document implements Cloneable, GetChars {

    private final static int MIN_GAP_SIZE = 32;
    private char[] textStore;
    private int gapStart;
    private int gapEnd;
    private int lineCount;
    private int allocMultiplier;
    private DocumentCache bufferCache;
    private UndoStack undoStack;

    private List<DocumentListener> documentListeners;
    private DocumentEvent event;

    private String encoding;
    private LineBreak lineBreak;

    public Document(@NonNull String text) {
        textStore = text.toCharArray();
        gapStart = textStore.length;
        gapEnd = textStore.length;
        allocMultiplier = 1;
        lineCount = lineCount(text);
        bufferCache = new DocumentCache();
        undoStack = new UndoStack(this);
        documentListeners = new ArrayList<>(1);
        encoding = "UTF-8";
        lineBreak = LineBreak.LF;
    }

    public Document() {
        textStore = new char[MIN_GAP_SIZE];
        allocMultiplier = 1;
        gapStart = 0;
        gapEnd = textStore.length;
        lineCount = 1;
        bufferCache = new DocumentCache();
        undoStack = new UndoStack(this);
        documentListeners = new ArrayList<>(1);
        encoding = "UTF-8";
        lineBreak = LineBreak.LF;
    }

    public void setEncoding(@NonNull String encoding) {
        this.encoding = encoding;
    }

    @NonNull
    public String getEncoding() {
        return encoding;
    }

    public void setLineBreak(@NonNull LineBreak lineBreak) {
        this.lineBreak = lineBreak;
    }

    @NonNull
    public LineBreak getLineBreak() {
        return lineBreak;
    }

    public void addDocumentListener(@NonNull DocumentListener listener) {
        if (!documentListeners.contains(listener))
            documentListeners.add(listener);
    }

    public void removeDocumentListener(@NonNull DocumentListener listener) {
        documentListeners.remove(listener);
    }

    private void sendDocumentChanging() {
        if (event != null) {
            for (DocumentListener listener : documentListeners) {
                listener.documentAboutToBeChanged(event);
            }
        }
    }

    private void sendDocumentChanged() {
        if (event != null) {
            event.document=this;
            for (DocumentListener listener : documentListeners) {
                listener.documentChanged(event);
            }
        }
    }

    public void setText(@NonNull String text) {
        event = new DocumentEvent(this, 0, getCharCount(), text);
        sendDocumentChanging();
        if (textStore.length < text.length()) {
            textStore =new char[text.length()+MIN_GAP_SIZE];
        }
        text.getChars(0,text.length(),textStore,0);
        gapStart = text.length();
        gapEnd = textStore.length;
        allocMultiplier = 1;
        lineCount = lineCount(text);
        bufferCache.invalidate();
        undoStack.reset();
        sendDocumentChanged();
    }

    public void insert(int offset, @NonNull String text) {
        insert(offset, text, System.nanoTime(), true);
    }

    public void delete(int offset, int length) {
        delete(offset, length, System.nanoTime(), true);
    }

    public void replace(int offset, int length, @NonNull String text) {
        replace(offset, length, text, System.nanoTime());
    }

    public int lineCount(@NonNull String text) {
        int line = 1;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                line++;
            } else if (c == '\r') {
                if (i + 1 < text.length() && text.charAt(i + 1) == '\n')
                    i++;
                line++;
            }
        }
        return line;
    }

    @NonNull
    public String getLineDelimiter(int line) {
        int start = getLineStart(line);
        int end = getCharCount();
        if (start < 0)
            start = 0;
        for (int i = end; i > start; i--) {
            if (charAt(i) == '\r') {
                return "\r";
            } else if (charAt(i) == '\n') {
                if (i - 1 > 0 && charAt(i - 1) == '\r') {
                    return "\r\n";
                }
                return "\n";
            }
        }
        return "\n";
    }


    @NonNull
    public String getLine(int line) {
        if (line < 0 || line > lineCount) return "";
        int start = getLineStart(line);
        int length = getLineLength(line);
        return getText(start, length);
    }

    @NonNull
    public String getPhysicalLine(int line) {
        if (line < 0 || line > lineCount) return "";
        int start = getLineStart(line);
        int length = getPhysicalLineLength(line);
        return getText(start, length);
    }

    public int getLineStart(int line) {
        if (line == 0) return 0;

        long cachedEntry = bufferCache.getNearestLine(line);
        int cachedLine = TextUtilities.unpackRangeStart(cachedEntry);
        int cachedOffset = TextUtilities.unpackRangeEnd(cachedEntry);

        int offset;
        if (line > cachedLine)
            offset = findCharOffset(line, cachedLine, cachedOffset);
        else if (line < cachedLine)
            offset = findCharOffsetBackward(line, cachedLine, cachedOffset);
        else
            offset = cachedOffset;

        if (offset >= 0)
            bufferCache.update(line, offset);
        return offset;
    }

    private int findCharOffset(int targetLine, int startLine, int startOffset) {
        int workingLine = startLine;
        int offset = startOffset;
        if (offset < 0)
            offset = 0;
        if (offset >= gapStart)
            offset += gapLength();

        while ((workingLine < targetLine) && (offset < textStore.length)) {
            if (textStore[offset] == '\n') {
                workingLine++;
            } else if (textStore[offset] == '\r') {
                workingLine++;
                if (offset + 1 < textStore.length && textStore[offset + 1] == '\n')
                    offset++;
            }
            offset++;

            if (offset == gapStart)
                offset = gapEnd;
        }

        if (workingLine != targetLine)
            return -1;

        if (offset >= gapStart)
            offset -= gapLength();
        return offset;
    }

    private int findCharOffsetBackward(int targetLine, int startLine, int startOffset) {
        if (targetLine == 0) return 0;

        int workingLine = startLine;
        int start = startOffset;
        if (start >= gapStart)
            start += gapLength();

        while (workingLine > (targetLine - 1) && start >= 0) {
            if (start == gapEnd)
                start = gapStart;

            start--;
            if (textStore[start] == '\n') {
                workingLine--;
                if (start - 1 >= 0 && textStore[start - 1] == '\r')
                    start--;
            }
        }

        int findOffset;
        if (start >= 0) {
            findOffset = start;
            if (findOffset >= gapStart)
                findOffset -= gapLength();

            ++findOffset;
        } else
            findOffset = -1;

        return findOffset;
    }


    public int findLineNumber(int offset) {
        if (offset < 0 || offset > getCharCount()) return -1;
        if (offset == 0) return 0;

        long cachedEntry = bufferCache.getNearestOffset(offset);
        int line = TextUtilities.unpackRangeStart(cachedEntry);
        int start = TextUtilities.unpackRangeEnd(cachedEntry);
        if (start >= gapStart)
            start += gapLength();

        int targetOffset = offset;
        if (targetOffset >= gapStart)
            targetOffset += gapLength();
        int lastKnownLine = -1;
        int lastKnownCharOffset = -1;

        if (targetOffset > start) {
            while ((start < targetOffset) && (start < textStore.length)) {
                if (textStore[start] == '\n') {
                    ++line;
                    lastKnownLine = line;
                    lastKnownCharOffset = start;
                    if (lastKnownCharOffset >= gapStart)
                        lastKnownCharOffset -= gapLength();
                    lastKnownCharOffset += 1;

                } else if (textStore[start] == '\r') {
                    line++;
                    lastKnownLine = line;

                    if (start + 1 < textStore.length
                            && textStore[start + 1] == '\n') {
                        start++;
                    }

                    lastKnownCharOffset = start;
                    if (lastKnownCharOffset >= gapStart)
                        lastKnownCharOffset -= gapLength();
                    lastKnownCharOffset += 1;
                }

                start++;
                if (start == gapStart)
                    start = gapEnd;
            }
        } else if (targetOffset < start) {
            while ((start > targetOffset) && (start > 0)) {
                if (start == gapEnd)
                    start = gapStart;

                start--;
                if (textStore[start] == '\n') {
                    lastKnownLine = line;
                    line--;
                    if (start - 1 > 0
                            && textStore[start - 1] == '\r') {
                        start--;
                    }
                    lastKnownCharOffset = start;
                    if (lastKnownCharOffset >= gapStart)
                        lastKnownCharOffset -= gapLength();
                    lastKnownCharOffset += 1;
                } else if (textStore[start] == '\r') {
                    lastKnownLine = line;
                    lastKnownCharOffset = start;
                    if (lastKnownCharOffset >= gapStart)
                        lastKnownCharOffset -= gapLength();
                    lastKnownCharOffset += 1;
                    line--;
                }
            }
        }

        if (start == targetOffset) {
            if (lastKnownLine != -1)
                bufferCache.update(lastKnownLine, lastKnownCharOffset);
            return line;
        } else
            return -1;
    }


    public int getLineLength(int line) {
        if (line < 0 || line > getLineCount()) return 0;
        int start = getLineStart(line);
        if (start < 0) return 0;

        int length = 0;
        if (start >= gapStart)
            start += gapLength();

        while (start < textStore.length) {
            char c = textStore[start];
            if (c == '\n')
                break;
            else if (c == '\r')
                break;
            start++;
            length++;
            if (start == gapStart)
                start = gapEnd;
        }
        return length;
    }


    public int getPhysicalLineLength(int line) {
        if (line < 0 || line > getLineCount()) return 0;
        int start = getLineStart(line);
        if (start < 0) return 0;

        int length = 0;

        if (start >= gapStart)
            start += gapLength();

        for (int i = start; i < textStore.length; i++) {
            char c = textStore[i];
            length++;
            if (c == '\r') {
                if (i + 1 < textStore.length) {
                    if (textStore[i + 1] == '\n')
                        length++;
                }
                break;
            } else if (c == '\n')
                break;

            if (i == gapStart)
                i = gapEnd;
        }
        return length;
    }


    public char charAt(int offset) {
        if (offset < 0 || offset > getCharCount()) return '\u0000';
        if (offset >= gapStart)
            return textStore[offset + gapLength()];
        else
            return textStore[offset];
    }

    @NonNull
    public String getText(int offset, int length) {
        if (offset < 0 || offset > getCharCount() || length <= 0)
            return "";

        if ((offset + length) > getCharCount())
            length = getCharCount() - offset;

        int end = offset + length;
        char[] chars = new char[length];
        getChars(offset, end, chars, 0);
        return new String(chars);
    }

    @NonNull
    public String getText() {
        return getText(0, getCharCount());
    }

    @NonNull
    protected String getGapSub(int charCount) {
        char[] chars = new char[charCount];
        if (gapStart < 0)
            return new String(chars);
        int end = gapStart + charCount;
        if (end >= textStore.length)
            end = textStore.length;
        arraycopy(textStore, gapStart, chars, 0, end - gapStart);
        return new String(chars);
    }

    protected void replace(int offset, int length, @NonNull String text, long time) {
        event = new DocumentEvent(this, offset, length, text);
        sendDocumentChanging();
        beginBatchEdit();
        if (length > 0)
            delete(offset, length, time, true, false);
        if (text.length() > 0)
            insert(offset, text, time, true, false);
        endBatchEdit();
        sendDocumentChanged();
    }

    protected void insert(int offset, String text, long time,
                          boolean undoable) {
        insert(offset, text, time, undoable, true);
    }

    private void insert(int offset, @NonNull String text, long time,
                        boolean undoable, boolean sendEvent) {
        if (undoable)
            undoStack.captureInsert(offset, text.length(), time);

        if (sendEvent) {
            event = new DocumentEvent(this, offset, 0, text);
            sendDocumentChanging();
        }

        int insertIndex = offset;
        if (insertIndex >= gapStart)
            insertIndex += gapLength();

        if (insertIndex != gapEnd) {
            if (insertIndex >= gapStart)
                adjustGapEnd(insertIndex);
            else
                adjustGapStart(insertIndex);
        }

        if (text.length() >= gapLength())
            growBufferBy(text.length() - gapLength());

        text.getChars(0, text.length(), textStore, gapStart);
        gapStart += text.length();

        lineCount += (lineCount(text) - 1);

        bufferCache.invalidateFrom(offset);
        if (sendEvent) {
            sendDocumentChanged();
        }
    }

    protected void delete(int offset, int length, long timestamp,
                          boolean undoable) {
        delete(offset, length
                , timestamp, undoable, true);
    }

    private void delete(int offset, int length, long timestamp,
                        boolean undoable, boolean sendEvent) {

        if (undoable)
            undoStack.captureDelete(offset, length, timestamp);

        if (sendEvent) {
            event = new DocumentEvent(this, offset, length, "");
            sendDocumentChanging();
        }

        int newGapStart = offset + length;
        if (newGapStart != gapStart) {
            if (newGapStart <= gapStart)
                adjustGapStart(newGapStart);
            else
                adjustGapEnd(newGapStart + gapLength());
        }

        int end = offset + length;
        for (int i = offset; i < end; i++) {
            gapStart--;
            if (textStore[gapStart] == '\n') {
                if (i + 1 < end && gapStart - 1 > 0 && textStore[gapStart - 1] == '\r') {
                    i++;
                    gapStart--;
                }
                lineCount--;
            } else if (textStore[gapStart] == '\r') {
                lineCount--;
            }
        }

        bufferCache.invalidateFrom(offset);
        if (sendEvent)
            sendDocumentChanged();
    }

    protected void shiftGapStart(int displacement) {
        if (displacement >= 0)
            lineCount += countLineCount(gapStart, displacement);
        else
            lineCount -= countLineCount(gapStart + displacement, -displacement);

        gapStart += displacement;
        int offset = gapStart - 1;
        if (offset >= gapStart)
            offset += gapLength();
        offset += 1;
        bufferCache.invalidateFrom(offset);
    }

    private int countLineCount(int start, int length) {
        int count = 0;
        int end = start + length;
        for (int i = start; i < end; i++) {
            if (textStore[i] == '\n') {
                count++;
            } else if (textStore[i] == '\r') {
                count++;
                if (i + 1 < end && textStore[i + 1] == '\n')
                    i++;
            }
        }
        return count;
    }

    private void adjustGapStart(int newGapStart) {
        while (gapStart > newGapStart) {
            gapEnd--;
            gapStart--;
            textStore[gapEnd] = textStore[gapStart];
        }
    }

    private void adjustGapEnd(int newGapEnd) {
        while (gapEnd < newGapEnd) {
            textStore[gapStart] = textStore[gapEnd];
            gapStart++;
            gapEnd++;
        }
    }


    private void growBufferBy(int minIncrement) {
        int increasedSize = minIncrement + MIN_GAP_SIZE * allocMultiplier;
        int newLength = textStore.length + increasedSize;
        char[] temp = new char[newLength];
        int i = 0;
        while (i < gapStart) {
            temp[i] = textStore[i];
            i++;
        }

        i = gapEnd;
        while (i < textStore.length) {
            temp[i + increasedSize] = textStore[i];
            i++;
        }

        gapEnd += increasedSize;
        textStore = temp;
        allocMultiplier <<= 1;
    }

    public int getCharCount() {
        return textStore.length - gapLength();
    }

    public int getLineCount() {
        return lineCount;
    }

    private int gapLength() {
        return gapEnd - gapStart;
    }


    public boolean isBatchEdit() {
        return undoStack.isBatchEdit();
    }

    public void beginBatchEdit() {
        undoStack.beginBatchEdit();
    }

    public void endBatchEdit() {
        undoStack.endBatchEdit();
    }

    public boolean undoable() {
        return undoStack.canUndo();
    }

    public boolean redoable() {
        return undoStack.canRedo();
    }

    public void undo() {
        undoStack.undo();
    }

    public void redo() {
        undoStack.redo();
    }


    @NonNull
    public CharSequence subSequence(int start, int end) {
        char[] chars = new char[end - start];
        getChars(start, end, chars, 0);
        return new String(chars);
    }

    @NonNull
    @Override
    public String toString() {
        char[] chars = new char[length()];
        getChars(0, length(), chars, 0);
        return String.valueOf(chars);
    }

    @Override
    public int length() {
        return textStore.length - gapLength();
    }

    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        if (end <= gapStart) {
            arraycopy(textStore, start, dest, destoff, end - start);
        } else if (start >= gapStart) {
            arraycopy(textStore, start + gapLength(), dest, destoff, end - start);
        } else {
            arraycopy(textStore, start, dest, destoff, gapStart - start);
            arraycopy(textStore, gapEnd,
                    dest, destoff + (gapStart - start),
                    end - gapStart);
        }
    }

    public void drawText(Canvas c, int start, int end, float x, float y, Paint p) {
        if (end <= gapStart) {
            c.drawText(textStore, start, end - start, x, y, p);
        } else if (start >= gapStart) {
            c.drawText(textStore, start + gapLength(), end - start, x, y, p);
        } else {
            char[] buf = TextUtilities.obtain(end - start);
            getChars(start, end, buf, 0);
            c.drawText(buf, 0, end - start, x, y, p);
            TextUtilities.recycle(buf);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void drawTextRun(Canvas c, int start, int end, int contextStart, int contextEnd,
                            float x, float y, boolean isRtl, Paint p) {
        int contextLen = contextEnd - contextStart;
        int len = end - start;
        if (contextEnd <= gapStart) {
            c.drawTextRun(textStore, start, len, contextStart, contextLen, x, y, isRtl, p);
        } else if (contextStart >= gapStart) {
            c.drawTextRun(textStore, start + gapLength(), len, contextStart + gapLength(),
                    contextLen, x, y, isRtl, p);
        } else {
            char[] buf = TextUtilities.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            c.drawTextRun(buf, start - contextStart, len, 0, contextLen, x, y, isRtl, p);
            TextUtilities.recycle(buf);
        }
    }

    public float measureText(int start, int end, Paint p) {
        float ret;
        if (end <= gapStart) {
            ret = p.measureText(textStore, start, end - start);
        } else if (start >= gapStart) {
            ret = p.measureText(textStore, start + gapLength(), end - start);
        } else {
            char[] buf = TextUtilities.obtain(end - start);
            getChars(start, end, buf, 0);
            ret = p.measureText(buf, 0, end - start);
            TextUtilities.recycle(buf);
        }
        return ret;
    }

    public int getTextWidths(int start, int end, float[] widths, Paint p) {
        int ret;
        if (end <= gapStart) {
            ret = p.getTextWidths(textStore, start, end - start, widths);
        } else if (start >= gapStart) {
            ret = p.getTextWidths(textStore, start + gapLength(), end - start, widths);
        } else {
            char[] buf = TextUtilities.obtain(end - start);
            getChars(start, end, buf, 0);
            ret = p.getTextWidths(buf, 0, end - start, widths);
            TextUtilities.recycle(buf);
        }
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public float getTextRunAdvances(int start, int end, int contextStart, int contextEnd,
                                    boolean isRtl,
                                    float[] advances, int advancesPos, @NonNull Paint p) {
        float ret;
        int contextLen = contextEnd - contextStart;
        int len = end - start;
        if (end <= gapStart) {
            ret = p.getTextRunAdvances(textStore, start, len, contextStart, contextLen,
                    isRtl, advances, advancesPos);
        } else if (start >= gapStart) {
            ret = p.getTextRunAdvances(textStore, start + gapLength(), len,
                    contextStart + gapLength(), contextLen, isRtl, advances, advancesPos);
        } else {
            char[] buf = TextUtilities.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            ret = p.getTextRunAdvances(buf, start - contextStart, len,
                    0, contextLen, isRtl, advances, advancesPos);
            TextUtilities.recycle(buf);
        }
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public int getTextRunCursor(int contextStart, int contextEnd, boolean isRtl, int offset,
                                int cursorOpt, @NonNull Paint p) {
        int ret;
        int contextLen = contextEnd - contextStart;
        if (contextEnd <= gapStart) {
            ret = p.getTextRunCursor(textStore, contextStart, contextLen,
                    isRtl, offset, cursorOpt);
        } else if (contextStart >= gapStart) {
            ret = p.getTextRunCursor(textStore, contextStart + gapLength(), contextLen,
                    isRtl, offset + gapLength(), cursorOpt) - gapLength();
        } else {
            char[] buf = TextUtilities.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            ret = p.getTextRunCursor(buf, 0, contextLen,
                    isRtl, offset - contextStart, cursorOpt) + contextStart;
            TextUtilities.recycle(buf);
        }
        return ret;
    }

    @NonNull
    @Override
    public Document clone() {
        try {
            Document clone = (Document) super.clone();
            clone.textStore = textStore;
            clone.gapStart = gapStart;
            clone.gapEnd = gapEnd;
            clone.allocMultiplier = allocMultiplier;
            clone.lineCount = lineCount;
            clone.bufferCache = new DocumentCache();
            clone.undoStack = new UndoStack(clone);
            clone.documentListeners = new ArrayList<>();
            clone.event = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}

