package com.apkide.editor;

import androidx.annotation.NonNull;

import com.apkide.common.Action;

public abstract class EditorAction implements Action {

    private final EditorView view;
    private boolean enabled;
    public EditorAction(EditorView view){
        this.view=view;
    }

    @NonNull
    public EditorView getView() {
        return view;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled=enabled;
    }
}
