package com.apkide.editor;

import com.apkide.common.text.TextContent;
import com.apkide.common.undo.AbstractUndoableEdit;
import com.apkide.common.undo.ExecutionException;
import com.apkide.common.undo.UndoManager;
import com.apkide.common.undo.UndoableEdit;
import com.apkide.common.undo.UndoableEditSupport;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EditorModel {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final TextContent content;
    private boolean batchEdit;
    private final UndoManager undoManager;
    private final UndoableEditSupport undoableEditSupport;

    public EditorModel() {
        content = new TextContent();
        undoManager = new UndoManager();
        undoableEditSupport = new UndoableEditSupport(this);
        undoableEditSupport.addUndoableEditListener(undoManager);
    }

    public void setText(String text) {
        try {
            lock.writeLock().lock();

            if (isCompoundEdit())
                endCompoundEdit();

            content.setText(text);
            undoManager.discardAllEdits();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void insert(int offset, String text) {
        try {
            lock.writeLock().lock();
            content.replaceTextRange(offset, 0, text);
            UndoableEdit edit = new InsertUndo(offset, text.length());
            undoableEditSupport.postEdit(edit);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(int offset, int length) {
        try {
            lock.writeLock().lock();
            String text = getText(offset, length);
            UndoableEdit edit = new DeleteUndo(offset, text);
            content.replaceTextRange(offset, 0, text);
            undoableEditSupport.postEdit(edit);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void replaceText(int offset, int length, String text) {

    }

    public String getLine(int line) {
        try {
            lock.readLock().lock();
            return content.getLine(line);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getLineCount() {
        try {
            lock.readLock().lock();
            return content.getLineCount();
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getText(int offset, int length) {
        try {
            lock.readLock().lock();
            return content.getTextRange(offset, length);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getCharCount() {
        try {
            lock.readLock().lock();
            return content.getCharCount();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean canUndo() {
        try {
            lock.readLock().lock();
            return undoManager.canUndo();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean canRedo() {
        try {
            lock.readLock().lock();
            return undoManager.canRedo();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void undo() throws ExecutionException {
        try {
            lock.writeLock().lock();
            undoManager.undo();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void redo() throws ExecutionException {
        try {
            lock.writeLock().lock();
            undoManager.redo();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setUndoLimit(int limit) {
        try {
            lock.writeLock().lock();
            undoManager.setLimit(limit);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getUndoLimit() {
        try {
            lock.readLock().lock();
            return undoManager.getLimit();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void beginCompoundEdit() {
        try {
            lock.writeLock().lock();
            batchEdit = true;
            undoableEditSupport.beginUpdate();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void endCompoundEdit() {
        try {
            lock.writeLock().lock();
            batchEdit = false;
            undoableEditSupport.endUpdate();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isCompoundEdit() {
        try {
            lock.readLock().lock();
            return batchEdit;
        } finally {
            lock.readLock().unlock();
        }
    }

    private class InsertUndo extends AbstractUndoableEdit {
        protected int offset;
        protected int length;
        protected String string;


        protected InsertUndo(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }

        @Override
        public void undo() throws ExecutionException {
            super.undo();
            try {
                string = getText(offset, length);
                delete(offset, length);
            } catch (Exception e) {
                throw new ExecutionException();
            }
        }

        @Override
        public void redo() throws ExecutionException {
            super.redo();
            try {
                insert(offset, string);
                string = null;
            } catch (Exception e) {
                throw new ExecutionException();
            }
        }
    }

    private class DeleteUndo extends AbstractUndoableEdit {
        protected int offset;
        protected int length;
        protected String string;

        protected DeleteUndo(int offset, String string) {
            this.offset = offset;
            this.string = string;
            this.length = string.length();
        }

        @Override
        public void undo() throws ExecutionException {
            super.undo();
            try {
                insert(offset, string);
                string = null;
            } catch (Exception e) {
                throw new ExecutionException();
            }
        }

        @Override
        public void redo() throws ExecutionException {
            super.redo();
            try {
                string = getText(offset, length);
                delete(offset, length);
            } catch (Exception e) {
                throw new ExecutionException();
            }
        }
    }

    private class ReplaceUndo extends AbstractUndoableEdit {
        protected int offset;
        protected int length;
        protected String string;

        public ReplaceUndo(int offset, int length, String string) {
            this.offset = offset;
            this.length = length;
            this.string = string;
        }

        @Override
        public void undo() throws ExecutionException {
            super.undo();
            try {

            } catch (Exception e) {
                throw new ExecutionException();
            }
        }

        @Override
        public void redo() throws ExecutionException {
            super.redo();
            try {

            } catch (Exception e) {
                throw new ExecutionException();
            }
        }
    }
}
