package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class Language {

    @NonNull
    public abstract String getName();

    @Nullable
    public abstract Highlighter getHighlighter();

    @NonNull
    public abstract String[] getDefaultFilePatterns();

}
