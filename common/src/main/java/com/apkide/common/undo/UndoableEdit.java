package com.apkide.common.undo;

public interface UndoableEdit {
    void undo()throws ExecutionException;

    boolean canUndo();

    void redo()throws ExecutionException;

    boolean canRedo();

    boolean isActive();

    void die();

    boolean addEdit(UndoableEdit edit);

    boolean replaceEdit(UndoableEdit edit);

    boolean isSignificant();
}
