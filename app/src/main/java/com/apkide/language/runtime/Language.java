package com.apkide.language.runtime;

import androidx.annotation.NonNull;

public interface Language {

    @NonNull
    String getName();

    @NonNull
    String[] getDefaultFilePatterns();

    Highlighter getHighlighter();

    Formatter getFormatter();

    CodeCompleter getCodeCompleter();
}
