package com.apkide.common.undo;

public interface UndoableEditLockSupport extends UndoableEdit {
    void lockEdit();

    void unlockEdit();
}