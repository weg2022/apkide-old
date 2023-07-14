package com.apkide.ui.views.editor;

import androidx.annotation.NonNull;

public interface ModelListener {

    void textSet(@NonNull Model model);

    void textPreInsert(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn,@NonNull CharSequence text);

    void textInserted(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence text);

    void textPreRemove(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn);

    void textRemoved(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn);

}
