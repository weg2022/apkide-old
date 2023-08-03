package com.apkide.ui.views.editor;

import androidx.annotation.NonNull;

public interface CaretListener {
    void caretMove(@NonNull Console console, int caretLine, int caretColumn, boolean isTyping);

}
