package com.apkide.language.service;

interface IFileEditor {

    int getLineIndentation(in int line);

    void indentationLine(in int line,in int indentationSize);

    int getIndentationSize();

    int getTabSize();

    int getLineCount();

    int getLineLength(in int line);

    char getChar(in int line,in int column);

    int getStyle(in int line,in int column);

    void insertLineBreak(in int line,in int column);

    void removeLineBreak(in int line);

    void insertText(in int line,in int column,in String text);

    void removeText(in int startLine,in int startColumn,in int endLine,in int endColumn);
}