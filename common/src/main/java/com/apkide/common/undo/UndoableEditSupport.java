package com.apkide.common.undo;

import com.apkide.common.SafeListenerList;

public class UndoableEditSupport {
    protected int updateLevel;
    protected CompoundEdit compoundEdit;
    protected SafeListenerList<UndoableEditListener> listeners;
    protected Object realSource;

    public UndoableEditSupport() {
        this((Object) null);
    }

    public UndoableEditSupport(Object source) {
        this.realSource = source == null ? this : source;
        this.updateLevel = 0;
        this.compoundEdit = null;
        this.listeners = new SafeListenerList<>();
    }

    public synchronized void addUndoableEditListener(UndoableEditListener l) {
        listeners.add(l);
    }

    public void removeUndoableEditListener(UndoableEditListener l) {
        listeners.remove(l);
    }

    public UndoableEditListener[] getUndoableEditListeners() {
        return (UndoableEditListener[]) listeners.getListeners();
    }

    protected void _postEdit(UndoableEdit e) {
        UndoableEditEvent ev = new UndoableEditEvent(realSource, e);
        for (UndoableEditListener listener : listeners) {
            listener.undoableEditHappened(ev);
        }
    }

    public synchronized void postEdit(UndoableEdit e) {
        if (updateLevel == 0) {
            _postEdit(e);
        } else {
            compoundEdit.addEdit(e);
        }
    }

    public int getUpdateLevel() {
        return updateLevel;
    }

    public synchronized void beginUpdate() {
        if (updateLevel == 0) {
            compoundEdit = createCompoundEdit();
        }

        ++updateLevel;
    }

    protected CompoundEdit createCompoundEdit() {
        return new CompoundEdit();
    }

    public synchronized void endUpdate() {
        --updateLevel;
        if (updateLevel == 0) {
            compoundEdit.end();
            _postEdit(compoundEdit);
            compoundEdit = null;
        }
    }
}