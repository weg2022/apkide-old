package com.apktool.cl.api.engine.formatter;

import java.io.Reader;

public interface CodeFormatterCallback {
    int getIndentation(int line);

    Reader getReader();

    void insertLineBreak(int line, int column);

    void insertSpace(int line, int column);

    void removeLineBreak(int startLine, int startColumn, int endLine, int endColumn);

    void removeSpace(int startLine, int startColumn, int endLine, int endColumn);

    void indentLine(int startLine, int endLine);

    void select(int startLine, int startColumn, int endLine, int endColumn);

    int getLineCount();

    int getLineWidth(int line);

    char getChar(int line, int column);

    void insertChar(int line, int column, char c);

    int getStyle(int line, int column);

    void insertChars(int line, int column, char[] var3);

    void removeChars(int line, int column, int length);

    void setCaretPosition(int line, int column);

    int getCaretColumn();

    int getCaretLine();

    void removeLineBreak(int line);
}

