package com.apkide.common.undo;

import java.util.Vector;

public class UndoManager extends CompoundEdit implements UndoableEditListener {
    int indexOfNextAdd = 0;
    int limit = 100;

    public UndoManager() {
        this.edits.ensureCapacity(this.limit);
    }

    public synchronized int getLimit() {
        return this.limit;
    }

    public synchronized void discardAllEdits() {
        for (UndoableEdit e : edits) {
            e.die();
        }

        edits = new Vector<>();
        indexOfNextAdd = 0;
    }

    protected void trimForLimit() {
        if (limit >= 0) {
            int size = edits.size();
            if (size > limit) {
                int halfLimit = limit / 2;
                int keepFrom = indexOfNextAdd - 1 - halfLimit;
                int keepTo = indexOfNextAdd - 1 + halfLimit;
                if (keepTo - keepFrom + 1 > limit)
                    ++keepFrom;


                if (keepFrom < 0) {
                    keepTo -= keepFrom;
                    keepFrom = 0;
                }

                if (keepTo >= size) {
                    int delta = size - keepTo - 1;
                    keepTo += delta;
                    keepFrom += delta;
                }

                trimEdits(keepTo + 1, size - 1);
                trimEdits(0, keepFrom - 1);
            }
        }

    }

    protected void trimEdits(int from, int to) {
        if (from <= to) {
            for(int i = to; from <= i; --i) {
                UndoableEdit e = edits.elementAt(i);
                e.die();
                edits.removeElementAt(i);
            }

            if (indexOfNextAdd > to)
                indexOfNextAdd -= to - from + 1;
            else if (indexOfNextAdd >= from)
                indexOfNextAdd = from;
        }

    }

    public synchronized void setLimit(int l) {
        if (!inProgress) {
            throw new RuntimeException("Attempt to call UndoManager.setLimit() after UndoManager.end() has been called");
        } else {
            limit = l;
            trimForLimit();
        }
    }

    protected UndoableEdit editToBeUndone() {
        int i = indexOfNextAdd;

        UndoableEdit edit;
        do {
            if (i <= 0)
                return null;

            --i;
            edit = edits.elementAt(i);
        } while(!edit.isSignificant());

        return edit;
    }

    protected UndoableEdit editToBeRedone() {
        int count = edits.size();
        int i = indexOfNextAdd;

        UndoableEdit edit;
        do {
            if (i >= count)
                return null;

            edit =edits.elementAt(i++);
        } while(!edit.isSignificant());

        return edit;
    }

    protected void undoTo(UndoableEdit edit) throws ExecutionException {
        UndoableEdit next;
        for(boolean done = false; !done; done = next == edit) {
            next = edits.elementAt(--indexOfNextAdd);
            next.undo();
        }

    }

    protected void redoTo(UndoableEdit edit) throws ExecutionException {
        UndoableEdit next;
        for(boolean done = false; !done; done = next == edit) {
            next = edits.elementAt(indexOfNextAdd++);
            next.redo();
        }

    }

    public void undoOrRedo() throws ExecutionException {
        tryUndoOrRedo(UndoManager.Action.ANY);
    }

    public synchronized boolean canUndoOrRedo() {
        return indexOfNextAdd == edits.size() ? canUndo() : canRedo();
    }

    public void undo() throws ExecutionException {
        tryUndoOrRedo(UndoManager.Action.UNDO);
    }

    public synchronized boolean canUndo() {
        if (!this.inProgress) {
            return super.canUndo();
        } else {
            UndoableEdit edit = this.editToBeUndone();
            return edit != null && edit.canUndo();
        }
    }

    public void redo() throws ExecutionException {
        this.tryUndoOrRedo(UndoManager.Action.REDO);
    }

    private void tryUndoOrRedo(Action action) throws ExecutionException {
        UndoableEditLockSupport lockSupport;
        boolean undo;
        synchronized(this) {
            if (action == UndoManager.Action.ANY)
                undo = indexOfNextAdd == edits.size();
             else
                undo = action == UndoManager.Action.UNDO;

            if (!inProgress) {
                if (undo)
                    super.undo();
                 else
                    super.redo();

                return;
            }

            UndoableEdit edit = undo ? editToBeUndone() : editToBeRedone();
            if (edit == null)
                throw new ExecutionException();

            lockSupport = this.getEditLockSupport(edit);
            if (lockSupport == null) {
                if (undo)
                    this.undoTo(edit);
                else
                    this.redoTo(edit);

                return;
            }
        }

        while(true) {
            lockSupport.lockEdit();
            UndoableEditLockSupport editLockSupport = null;

            try {
                synchronized(this) {
                    if (action == UndoManager.Action.ANY)
                        undo = indexOfNextAdd == this.edits.size();

                    if (!inProgress) {
                        if (undo)
                            super.undo();
                         else
                            super.redo();

                        return;
                    }

                    UndoableEdit edit = undo ? editToBeUndone() : editToBeRedone();
                    if (edit == null)
                        throw  new ExecutionException();

                    editLockSupport = getEditLockSupport(edit);
                    if (editLockSupport != null && editLockSupport != lockSupport)
                        continue;

                    if (undo)
                        this.undoTo(edit);
                     else
                        this.redoTo(edit);
                }

            } finally {
                lockSupport.unlockEdit();
                lockSupport = editLockSupport;
            }

            return;
        }
    }

    private UndoableEditLockSupport getEditLockSupport(UndoableEdit anEdit) {
        return anEdit instanceof UndoableEditLockSupport ? (UndoableEditLockSupport)anEdit : null;
    }

    public synchronized boolean canRedo() {
        if (!inProgress) {
            return super.canRedo();
        } else {
            UndoableEdit edit = editToBeRedone();
            return edit != null && edit.canRedo();
        }
    }

    public synchronized boolean addEdit(UndoableEdit anEdit) {
        trimEdits(indexOfNextAdd, edits.size() - 1);
        boolean retVal = super.addEdit(anEdit);
        if (inProgress)
            retVal = true;

        indexOfNextAdd = edits.size();
        trimForLimit();
        return retVal;
    }

    public synchronized void end() {
        super.end();
        trimEdits(indexOfNextAdd,edits.size() - 1);
    }


    public void undoableEditHappened(UndoableEditEvent e) {
        this.addEdit(e.getEdit());
    }

    private enum Action {
        UNDO,
        REDO,
        ANY;
    }
}