package com.apkide.ui.views.editor.schemes;

import androidx.annotation.NonNull;

import com.apkide.ui.views.editor.Theme;

public class DarkScheme implements Theme.ColorScheme {
    @NonNull
    @Override
    public String getName() {
        return "Dark";
    }

    @Override
    public long getVersion() {
        return 0;
    }

    @Override
    public void applyDefault(Theme theme) {
        //TODO: Implementation
    }
}
