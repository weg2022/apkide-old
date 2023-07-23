package com.apkide.openapi.language.api.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.api.FileEntry;

import org.antlr.v4.runtime.tree.ParseTree;

public interface StructureCallback {
    void fileStarted(@NonNull FileEntry file);

    void elementFound(@NonNull FileEntry file,
                   @NonNull ParseTree element,
                   int identifierLine,
                   int identifierStartColumn,
                   int identifierEndColumn,
                   int startLine,
                   int startColumn,
                   int endLine,
                   int endColumn,
                   int node);

    void elementFound(@NonNull FileEntry file,
                      @NonNull ParseTree element,
                      int identifierLine,
                      int identifierStartColumn,
                      int identifierEndColumn,
                      int startLine,
                      int startColumn,
                      int endLine,
                      int endColumn
    );

    void fileFinished(@NonNull FileEntry file);
}
