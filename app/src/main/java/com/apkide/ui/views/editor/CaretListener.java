package com.apkide.ui.views.editor;

import androidx.annotation.NonNull;

public interface CaretListener {
    void caretUpdate(@NonNull EditorView view,int line,int column,boolean typing);
}
