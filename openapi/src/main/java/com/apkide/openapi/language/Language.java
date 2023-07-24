package com.apkide.openapi.language;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.openapi.language.api.FileEntry;
import com.apkide.openapi.language.api.Highlights;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
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
    Lexer getLexer();

    @Nullable
    Parser getParser();

    @Nullable
    Analyzer getAnalyzer();

    @Nullable
    Tools getTools();

    boolean isArchiveFileSupported();

    @Nullable
    String[] getArchiveEntries(@NonNull String filePath) throws IOException;

    @Nullable
    Reader getArchiveEntryReader(@NonNull String filePath, @NonNull String entryPath, @NonNull String encoding) throws IOException;

    long getArchiveVersion(@NonNull String filePath);

    void closeArchiveFile();

    void processVersion(@NonNull FileEntry file);

    void fileHighlighting(@NonNull FileEntry file, @NonNull Reader reader, @NonNull ResultCallback<Highlights> callback) throws IOException;

    void semanticHighlighting(@NonNull FileEntry file,@NonNull Language language,@Nullable SyntaxTree ast)throws IOException;

    void createSyntaxTree(@NonNull Reader reader, @NonNull FileEntry file, long syntaxVersion, @NonNull ResultCallback<SyntaxTree> callback, boolean errors, boolean parseCode, boolean parseComments) throws IOException;

}
