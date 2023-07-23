package com.apkide.openapi.language.api.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.api.FileEntry;

import org.antlr.v4.runtime.tree.ParseTree;

public interface CodeCompleterCallback {

    void fileStarted(@NonNull FileEntry file);

    void listStarted();

    void listElementUnknownIdentifierFound(@NonNull FileEntry file,@NonNull String identifier);

    void listElementKeywordFound(@NonNull FileEntry file, @NonNull String keyword);

    void listElementFound(@NonNull FileEntry file, @NonNull ParseTree element);

    void listElementFound(@NonNull FileEntry file, @NonNull ParseTree element, boolean enclosing);

    void listCompleted(@NonNull FileEntry file, int line, int column);

    void fileFinished(@NonNull FileEntry file);
}
