package com.apkide.common.undo;

import java.util.EventListener;

public interface UndoableEditListener extends EventListener {
    void undoableEditHappened(UndoableEditEvent event);
}
