package com.apkide.openapi.language;

import androidx.annotation.NonNull;

import java.io.Reader;
import java.util.List;
import java.util.Set;

public interface Formatter {
    @NonNull
    List<? extends FormatterOption> getAllOptions();

    @NonNull
    Set<String> getDefaultOptions();

    void setOptions(@NonNull Set<String> options);

    @NonNull
    Set<String> getOptions();

    void outCommentLines(@NonNull FormatterCallback callback, int startLine, int endLine);

    void unOutCommentLines(@NonNull FormatterCallback callback, int startLine, int endLine);

    void expandSelection(@NonNull FormatterCallback callback, int startLine, int startColumn, int endLine, int endColumn);

    void expandSelectionToStatements(@NonNull FormatterCallback callback, int startLine, int startColumn, int endLine, int endColumn);

    void learnStyle(@NonNull FormatterCallback callback, @NonNull Set<String> options);

    void autoIndentAfterPaste(@NonNull FormatterCallback callback, int startLine, int endLine);

    void autoFormatLines(@NonNull FormatterCallback callback, int startLine, int endLine);

    void autoIndentLines(@NonNull FormatterCallback callback, int startLine, int endLine);

    void autoIndentLine(@NonNull FormatterCallback callback, int line);

    void indentAfterLineBreak(@NonNull FormatterCallback callback, int line, int column);

    void indentAfterCharEvent(@NonNull FormatterCallback callback, int line, int column, char c);

    interface FormatterOption {

        @NonNull
        String getGroup();

        @NonNull
        String getName();

        @NonNull
        String getPreview(boolean preview);

    }

    interface FormatterCallback {

        void setCaretPosition(int line, int column);

        int getCaretLine();

        int getCaretColumn();

        void select(int startLine, int startColumn, int endLine, int endColumn);

        @NonNull
        Reader getReader();

        char getChar(int line, int column);

        int getLineCount();

        int getLineLength(int line);

        int getStyle(int line, int column);

        void setIndentationSize(int indentationSize);

        int getIndentationSize();

        void setTabSize(int tabSize);

        int getTabSize();

        void setLineMaxColumn(int maxColumn);

        int getLineMaxColumn();

        int getIndentation(int line);

        void indentationLine(int line, int indentationSize);

        void insertLineBreak(int line, int column);

        void removeLineBreak(int line);

        void removeLineBreak(int startLine, int startColumn, int endLine, int endColumn);

        void insertSpace(int line, int column);

        void removeSpace(int startLine, int startColumn, int endLine, int endColumn);

        void insertChar(int line, int column, char c);

        void insertChars(int line, int column, char[] chars);

        void removeChars(int startLine, int startColumn, int endLine, int endColumn);
    }
}
