package com.apkide.ui.views.editor;

public interface SelectionListener {
    void selectionUpdate(EditorView view);
    
    void selectionChanged(EditorView view, boolean selectionVisibility);
}
