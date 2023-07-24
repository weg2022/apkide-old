package com.apkide.openapi.language;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.api.FileEntry;

import org.antlr.v4.runtime.tree.SyntaxTree;

public interface Analyzer {

    void analyzeErrors(@NonNull FileEntry file, @NonNull SyntaxTree ast);
}
