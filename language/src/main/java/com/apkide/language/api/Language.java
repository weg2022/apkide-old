package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public  interface Language {

    @NonNull
    String getName();

    @Nullable
    Highlighter getHighlighter();

    @NonNull
    String[] getDefaultFilePatterns();

}
