package com.apkide.common.undo;

import java.util.EventObject;

public class UndoableEditEvent extends EventObject {
    private final UndoableEdit myEdit;

    public UndoableEditEvent(Object source, UndoableEdit edit) {
        super(source);
        this.myEdit = edit;
    }

    public UndoableEdit getEdit() {
        return this.myEdit;
    }
}
