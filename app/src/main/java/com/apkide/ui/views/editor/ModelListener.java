package com.apkide.ui.views.editor;

import androidx.annotation.NonNull;

import java.util.EventListener;

public interface ModelListener extends EventListener {

    void insertUpdate(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn);

    void removeUpdate(@NonNull Model model,int startLine,int startColumn,int endLine,int endColumn);

}
