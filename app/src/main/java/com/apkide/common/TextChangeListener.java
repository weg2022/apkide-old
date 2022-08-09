package com.apkide.common;

import androidx.annotation.NonNull;

public interface TextChangeListener {
    void textSet(@NonNull TextChangeEvent event);
    
    void textChanging(@NonNull TextChangeEvent event);

    void textChanged(@NonNull TextChangeEvent event);
}
