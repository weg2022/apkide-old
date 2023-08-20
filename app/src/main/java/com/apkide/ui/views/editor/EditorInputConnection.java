package com.apkide.ui.views.editor;

import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;

public class EditorInputConnection extends BaseInputConnection {
    private final EditorView view;
    public EditorInputConnection(EditorView view) {
        super(view,true);
        this.view = view;
    }
    
    
    @Override
    public boolean beginBatchEdit() {
        return super.beginBatchEdit();
    }
    
    @Override
    public boolean endBatchEdit() {
        return super.endBatchEdit();
    }
    
    
    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        for (int i = 0; i < text.length(); i++) {
             view.insertChar(text.charAt(i));
        }
        return super.commitText(text, newCursorPosition);
    }
    
    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        return super.sendKeyEvent(event);
    }
    
    
}
