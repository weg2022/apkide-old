package com.apkide.editor.text;

import androidx.annotation.NonNull;

public interface TextChangeListener {
    
    default void textPrepareInsertion(@NonNull TextModel model, int offset, @NonNull String newText) {
    
    }
    
    void textInsertionUpdate(@NonNull TextModel model, int offset, @NonNull String newText);
    
    default void textPrepareDeletion(@NonNull TextModel model, int start, int end) {
    
    }
    
    void textDeletionUpdate(@NonNull TextModel model, int start, int end);
}
