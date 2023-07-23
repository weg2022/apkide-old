package com.apkide.openapi.language.api.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.Language;
import com.apkide.openapi.language.api.FileEntry;
import com.apkide.openapi.language.api.Highlights;

public interface HighlighterCallback {

    void fileStarted(@NonNull FileEntry file);

    void fileHighlightingFinished(@NonNull FileEntry file, @NonNull Language language, @NonNull Highlights highlights);

    void keywordFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void packageFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void fieldFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void variableFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void functionFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void functionCallFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void parameterFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void identifierFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void constructorFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void typeFound(@NonNull FileEntry file, @NonNull Language language, int startLine, int startColumn, int endLine, int endColumn);

    void semanticHighlightingFinished(@NonNull FileEntry file);

    void fileFinished(@NonNull FileEntry file);
}
