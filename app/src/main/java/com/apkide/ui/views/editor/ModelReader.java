package com.apkide.ui.views.editor;

import java.io.Reader;

class ModelReader extends Reader {
    private final Model myModel;
    private int myStartLine;
    private int myStartColumn;
    private int myEndLine;
    private int myEndColumn;
    private final char myTabChar;
    private final char[] myLineBreak;
    private int myColumn;

    ModelReader(Model model, int startLine, int startColumn, int endLine, int endColumn, boolean useSpaces, String lineBreak) {
        myModel = model;
        myStartLine = startLine;
        myStartColumn = startColumn;
        myEndLine = endLine;
        myEndColumn = endColumn;

        if (myEndLine >= model.getLineCount())
            myEndLine = model.getLineCount() - 1;

        if (myEndColumn > model.getLineLength(myEndLine))
            myEndColumn = model.getLineLength(myEndLine);

        myColumn = 0;
        myTabChar = useSpaces ? ' ' : '\t';
        myLineBreak = lineBreak.toCharArray();
    }

    @Override
    public int read() {
        synchronized (myModel) {
            int lineCount = myModel.getLineCount();
            if (myStartLine < myEndLine && myStartLine < lineCount) {
                if (myStartColumn < myModel.getLineLength(myStartLine)) {
                    int column = myStartColumn;
                    myStartColumn++;
                    char c = myModel.getChar(myStartLine, column);
                    if (c == '\t')
                        return myTabChar;
                    return c;
                } else if (myColumn < myLineBreak.length - 1) {
                    int column = myColumn;
                    myColumn++;
                    return myLineBreak[column];
                } else {
                    myColumn = 0;
                    myStartColumn = 0;
                    myStartLine++;
                    return myLineBreak[myLineBreak.length - 1];
                }
            } else if (myStartLine != myEndLine || myStartLine >= lineCount) {
                return -1;
            } else {
                if (myStartColumn > myEndColumn || myStartColumn > myModel.getLineLength(myStartLine)) {
                    return -1;
                }

                if (myStartColumn < myModel.getLineLength(myStartLine)) {
                    int column = myStartColumn;
                    myStartColumn++;
                    char c = myModel.getChar(myStartLine, column);
                    if (c == '\t') {
                        return myTabChar;
                    }
                    return c;
                } else if (myColumn < myLineBreak.length - 1) {
                    int column = myColumn;
                    myColumn++;
                    return myLineBreak[column];
                } else {
                    myColumn = 0;
                    myStartColumn = 0;
                    myStartLine++;
                    return myLineBreak[myLineBreak.length - 1];
                }
            }
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) {
        if (cbuf == null)
            throw new NullPointerException();
        if (len < 0)
            throw new IndexOutOfBoundsException();
        if (off >= 0) {
            if (off + len > cbuf.length)
                throw new IndexOutOfBoundsException();
            if (len == 0) return -1;
            int read = read();
            if (read == -1) {
                return -1;
            }

            int next = off + 1;
            cbuf[off] = (char) read;
            int length = 1;
            while (length < len) {
                int readInt = read();
                if (readInt == -1) break;

                cbuf[next] = (char) readInt;
                length++;
                next++;
            }
            return length;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void close() {

    }
}
