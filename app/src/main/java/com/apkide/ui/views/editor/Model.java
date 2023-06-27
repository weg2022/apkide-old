package com.apkide.ui.views.editor;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.apkide.common.text.TextChangeListener;
import com.apkide.common.text.TextContent;
import com.apkide.common.text.TextGraphics;
import com.apkide.common.undo.AbstractUndoableEdit;
import com.apkide.common.undo.ExecutionException;
import com.apkide.common.undo.UndoManager;
import com.apkide.common.undo.UndoableEditSupport;

public class Model implements TextGraphics {

    protected final Object lock = new Object();

    private final TextContent textContent = new TextContent();

    private final UndoManager undoManager = new UndoManager();

    private final UndoableEditSupport undoableEditSupport;

    private boolean readOnly;
    private boolean compoundEdit;

    public Model() {
        undoableEditSupport = new UndoableEditSupport(this);
        undoableEditSupport.addUndoableEditListener(undoManager);
    }

    public void setText(String text) {
        synchronized (lock) {
            undoManager.discardAllEdits();

            textContent.setText(text);

        }
    }

    public void insert(int offset, String text) {
        if (text == null || text.length() == 0) return;

        synchronized (lock) {

            textContent.replaceTextRange(offset, 0, text);

            undoableEditSupport.postEdit(new InsertEdit(offset, text.length()));

        }
    }

    public void delete(int offset, int length) {
        if (length <= 0) return;

        synchronized (lock) {

            String text = getTextRange(offset, length);
            textContent.replaceTextRange(offset, length, "");

            undoableEditSupport.postEdit(new DeleteEdit(offset, text));
        }
    }

    public void replace(int offset, int length, String text) {
        delete(offset, length);
        insert(offset, text);
    }

    public char getChar(int offset) {
        synchronized (lock) {
            return textContent.charAt(offset);
        }
    }

    public void getChars(int offset, int length, char[] dest, int destoff) {
        synchronized (lock) {
            textContent.getChars(offset, length, dest, destoff);
        }
    }

    public String getTextRange(int offset, int length) {
        synchronized (lock) {
            return textContent.getTextRange(offset, length);
        }
    }

    public String getText() {
        synchronized (lock) {
            return textContent.getText();
        }
    }

    public int getCharCount() {
        synchronized (lock) {
            return textContent.getCharCount();
        }
    }

    public int getLineStart(int line) {
        synchronized (lock) {
            return textContent.getOffsetAtLine(line);
        }
    }

    public int getLineIndex(int offset) {
        synchronized (lock) {
            return textContent.getLineAtOffset(offset);
        }
    }

    public String getLineText(int line) {
        synchronized (lock) {
            return textContent.getLine(line);
        }
    }

    public int getLineLength(int line) {
        synchronized (lock) {
            return textContent.getLineLength(line);
        }
    }

    public int getLineCount() {
        synchronized (lock) {
            return textContent.getLineCount();
        }
    }

    public void addTextChangeListener(TextChangeListener listener) {
        textContent.addTextChangeListener(listener);
    }

    public void removeTextChangeListener(TextChangeListener listener) {
        textContent.removeTextChangeListener(listener);
    }

    public void setReadOnly(boolean readOnly) {
        synchronized (lock) {
            this.readOnly = readOnly;
        }
    }

    public boolean isReadOnly() {
        synchronized (lock) {
            return readOnly;
        }
    }

    public boolean canUndo() {
        synchronized (lock) {
            return undoManager.canUndo();
        }
    }

    public boolean canRedo() {
        synchronized (lock) {
            return undoManager.canRedo();
        }
    }

    public void undo() throws ExecutionException {
        synchronized (lock) {
            undoManager.undo();
        }
    }


    public void redo() throws ExecutionException {
        synchronized (lock) {
            undoManager.redo();
        }
    }

    public void setUndoHistoryLimit(int limit) {
        synchronized (lock) {
            undoManager.setLimit(limit);
        }
    }

    public int getUndoHistoryLimit() {
        synchronized (lock) {
            return undoManager.getLimit();
        }
    }

    public void beginCompoundEdit() {
        synchronized (lock) {
            compoundEdit = true;
            undoableEditSupport.beginUpdate();
        }
    }

    public void endCompoundEdit() {
        synchronized (lock) {
            compoundEdit = false;
            undoableEditSupport.endUpdate();
        }
    }

    public boolean isCompoundEdit() {
        synchronized (lock) {
            return compoundEdit;
        }
    }

    @Override
    public void nativeDrawText(Canvas canvas, int offset, int length, float x, float y, Paint paint) {
        synchronized (lock) {
            textContent.nativeDrawText(canvas, offset, length, x, y, paint);
        }
    }

    @Override
    public int nativeGetTextWidths(int offset, int length, float[] widths, Paint paint) {
        synchronized (lock) {
            return textContent.nativeGetTextWidths(offset, length, widths, paint);
        }
    }

    @Override
    public float nativeMeasureText(int offset, int length, Paint paint) {
        synchronized (lock) {
            return textContent.nativeMeasureText(offset, length, paint);
        }
    }


    private class InsertEdit extends AbstractUndoableEdit {
        private final int start;
        private final int length;
        private String text;

        public InsertEdit(int start, int length) {
            this.start = start;
            this.length = length;
        }

        @Override
        public void undo() throws ExecutionException {
            super.undo();
            try {
                text = getTextRange(start, length);
                delete(start, length);
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }

        @Override
        public void redo() throws ExecutionException {
            super.redo();
            try {
                insert(start, text);
                text = null;
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }
    }

    private class DeleteEdit extends AbstractUndoableEdit {
        private int start;
        private int length;
        private String text;

        public DeleteEdit(int start, String text) {
            this.start = start;
            this.text = text;
            this.length = text.length();
        }

        @Override
        public void undo() throws ExecutionException {
            super.undo();
            try {
                insert(start, text);
                text = null;
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }

        @Override
        public void redo() throws ExecutionException {
            super.redo();
            try {
                text = getTextRange(start, length);
                delete(start, length);
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }
    }
}
