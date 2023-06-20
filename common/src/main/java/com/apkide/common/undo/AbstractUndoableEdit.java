package com.apkide.common.undo;

public class AbstractUndoableEdit implements UndoableEdit {

    boolean hasBeenDone = true;
    boolean alive = true;

    @Override
    public void undo() throws ExecutionException {
        if (!canUndo()) {
            throw new ExecutionException();
        } else {
            hasBeenDone = false;
        }
    }

    @Override
    public boolean canUndo() {
        return alive && hasBeenDone;
    }

    @Override
    public void redo() throws ExecutionException {
        if (!canRedo()) {
            throw new ExecutionException();
        } else {
            hasBeenDone = true;
        }
    }

    @Override
    public boolean canRedo() {
        return alive && !hasBeenDone;
    }

    @Override
    public boolean isActive() {
        return alive;
    }

    @Override
    public void die() {
        alive = false;
    }

    @Override
    public boolean addEdit(UndoableEdit edit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit edit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }
}
