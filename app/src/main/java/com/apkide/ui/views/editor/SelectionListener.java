package com.apkide.ui.views.editor;

import androidx.annotation.NonNull;

public interface SelectionListener {
    void selectUpdate(@NonNull Console console);

    void selectChanged(@NonNull Console console,boolean select);
}
