package com.apkide.openapi.language;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.openapi.language.api.FileEntry;
import com.apkide.openapi.language.api.Highlights;

import org.antlr.v4.runtime.tree.SyntaxTree;

import java.io.IOException;
import java.io.Reader;

public interface Language {

    @NonNull
    String getName();

    @NonNull
    String[] getDefaultFilePatterns();

    @Nullable
    Formatter getFormatter();

    @Nullable
    Syntax getSyntax();

    @Nullable
    Lexer getLexer();

    @Nullable
    Parser getParser();

    @Nullable
    Analyzer getAnalyzer();

    @Nullable
    Renderer getRenderer();

    boolean isArchiveFileSupported();

    @Nullable
    String[] getArchiveEntries(@NonNull String filePath) throws IOException;

    @NonNull
    Reader getArchiveEntryReader(@NonNull String filePath, @NonNull String entryPath, @NonNull String encoding) throws IOException;

    long getArchiveVersion(@NonNull String filePath);

    void closeArchiveFile();

    void fileHighlighting(@NonNull FileEntry file, @NonNull Reader reader, @NonNull Highlights highlights);

    void createSyntaxTree(@NonNull FileEntry file, @NonNull Reader reader, @NonNull SyntaxTree[] syntaxTrees, boolean checkErrors);
}
