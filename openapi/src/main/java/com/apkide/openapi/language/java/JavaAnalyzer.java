package com.apkide.openapi.language.java;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.Analyzer;
import com.apkide.openapi.language.api.FileEntry;
import com.apkide.openapi.language.api.Model;

import org.antlr.v4.runtime.tree.SyntaxTree;

public class JavaAnalyzer implements Analyzer {
    private final Model myModel;
    private final JavaLanguage myLanguage;

    public JavaAnalyzer(Model model, JavaLanguage language) {
        myModel = model;
        myLanguage = language;
    }

    @Override
    public void analyzeErrors(@NonNull FileEntry file, @NonNull SyntaxTree ast) {

    }
}
