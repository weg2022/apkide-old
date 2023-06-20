package com.apkide.common.undo;

import java.util.Enumeration;
import java.util.Vector;

public class CompoundEdit extends AbstractUndoableEdit {
    boolean inProgress = true;
    protected Vector<UndoableEdit> edits = new Vector<>();

    @Override
    public void undo() throws ExecutionException {
        super.undo();
        int i = edits.size();
        while (i-- > 0) {
            UndoableEdit edit = edits.elementAt(i);
            edit.undo();
        }
    }

    @Override
    public void redo() throws ExecutionException {
        super.redo();
        Enumeration<UndoableEdit> cursor = edits.elements();
        while (cursor.hasMoreElements()) {
            cursor.nextElement().redo();
        }
    }

    protected UndoableEdit lastEdit() {
        int count = edits.size();
        return count > 0 ? edits.elementAt(count - 1) : null;
    }

    @Override
    public void die() {
        int size = edits.size();

        for (int i = size - 1; i >= 0; --i) {
            UndoableEdit e = edits.elementAt(i);
            e.die();
        }
        super.die();
    }

    @Override
    public boolean addEdit(UndoableEdit edit) {
        if (!inProgress) {
            return false;
        } else {
            UndoableEdit last = lastEdit();
            if (last == null) {
                edits.addElement(edit);
            } else if (!last.addEdit(edit)) {
                if (edit.replaceEdit(last)) {
                    edits.removeElementAt(edits.size() - 1);
                }

                edits.addElement(edit);
            }

            return true;
        }
    }

    public void end() {
        inProgress = false;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    @Override
    public boolean canUndo() {
        return !isInProgress() && super.canUndo();
    }

    @Override
    public boolean canRedo() {
        return !isInProgress() && super.canRedo();
    }

    @Override
    public boolean isSignificant() {
        Enumeration<UndoableEdit> cursor = edits.elements();
        if (!cursor.hasMoreElements())
            return false;
        while (!cursor.nextElement().isSignificant()) {
            if (!cursor.hasMoreElements())
                return false;
        }
        return true;
    }
}
