package com.apkide.openapi.language.java;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.Formatter;
import com.apkide.openapi.language.api.Model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaFormatter implements Formatter {
    private final Model myModel;
    private final JavaLanguage myLanguage;
    private final Set<String> options = new HashSet<>();

    public JavaFormatter(Model model, JavaLanguage language) {
        myModel = model;
        myLanguage = language;
    }

    @NonNull
    @Override
    public List<? extends FormatterOption> getAllOptions() {
        return JavaFormatterOption.getAll();
    }

    @NonNull
    @Override
    public Set<String> getDefaultOptions() {
        return JavaFormatterOption.getDefaultOptions();
    }

    @Override
    public void setOptions(@NonNull Set<String> options) {
        this.options.clear();
        this.options.addAll(options);
    }

    @NonNull
    @Override
    public Set<String> getOptions() {
        return options;
    }

    @Override
    public void outCommentLines(@NonNull FormatterCallback callback, int startLine, int endLine) {
        if (startLine == endLine) {
            callback.insertChar(startLine, 0, '/');
            callback.insertChar(startLine, 1, '/');
            return;
        }
        callback.insertChar(startLine, 0, '/');
        callback.insertChar(startLine, 1, '*');
        for (int i = startLine + 1; i < endLine; i++) {
            callback.insertSpace(i, 0);
            callback.insertChar(i, 1, '*');
        }
        callback.insertChar(endLine, 0, '*');
        callback.insertChar(endLine, 1, '/');
    }

    @Override
    public void unOutCommentLines(@NonNull FormatterCallback callback, int startLine, int endLine) {
        if (startLine == endLine) {
            if (callback.getLineLength(startLine) >= 2) {
                char first = callback.getChar(startLine, 0);
                char second = callback.getChar(startLine, 1);
                if ((first == '/' && second == '*') || (first == '/' && second == '/')) {
                    callback.removeChars(startLine, 0, startLine, 2);
                }
                return;
            }
        }
        for (int i = startLine; i < endLine; i++) {
            if (callback.getLineLength(i) >= 2) {
                char first = callback.getChar(i, 0);
                char second = callback.getChar(i, 1);
                if ((first == '/' && second == '*') || (first == '/' && second == '/'))
                    callback.removeChars(i, 0, i, 2);
            }
        }
    }

    @Override
    public void expandSelection(@NonNull FormatterCallback callback, int startLine, int startColumn, int endLine, int endColumn) {

    }

    @Override
    public void expandSelectionToStatements(@NonNull FormatterCallback callback, int startLine, int startColumn, int endLine, int endColumn) {

    }

    @Override
    public void learnStyle(@NonNull FormatterCallback callback, @NonNull Set<String> options) {

    }

    @Override
    public void autoIndentAfterPaste(@NonNull FormatterCallback callback, int startLine, int endLine) {

    }

    @Override
    public void autoFormatLines(@NonNull FormatterCallback callback, int startLine, int endLine) {

    }

    @Override
    public void autoIndentLines(@NonNull FormatterCallback callback, int startLine, int endLine) {

    }

    @Override
    public void autoIndentLine(@NonNull FormatterCallback callback, int line) {

    }

    @Override
    public void indentAfterLineBreak(@NonNull FormatterCallback callback, int line, int column) {

    }

    @Override
    public void indentAfterCharEvent(@NonNull FormatterCallback callback, int line, int column, char c) {

    }
}
