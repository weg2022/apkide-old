package com.apkide.ui.views.editor;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class StyleSpan {
    private byte[][] myLines = new byte[100][5];
    
    private int getSize(int line) {
        if (line >= this.myLines.length) {
            return 0;
        }
        byte[] styles = this.myLines[line];
        if (styles.length == 0) {
            return 0;
        }
        byte lastStyle = styles[styles.length - 1];
        for (int length = styles.length - 2; length >= 0; length--) {
            if (styles[length] != lastStyle) {
                return length + 2;
            }
        }
        return 0;
    }
    
    private void insert(int line, int column) {
        int endLine = line + 2;
        if (this.myLines.length <= endLine) {
            return;
        }
        int startLine = line + 1;
        System.arraycopy(this.myLines, startLine, this.myLines, endLine, (this.myLines.length - line) - 2);
        byte[] styles = this.myLines[line];
        byte[] newStyles = new byte[styles.length];
        this.myLines[startLine] = newStyles;
        if (styles.length > column) {
            byte style = styles[column];
            int newStyleIndex = 0;
            int index = column;
            while (index < styles.length) {
                newStyles[newStyleIndex] = styles[index];
                styles[index] = style;
                index++;
                newStyleIndex++;
            }
        }
    }
    
    private void insert(int line, int startColumn, int endColumn) {
        int col;
        if (endColumn != 0 && this.myLines.length > line && (col = getSize(line)) > startColumn) {
            if (col - 1 == startColumn && startColumn > 0) {
                resize(line, col + 1);
                byte[] styles = this.myLines[line];
                byte lastStyle = styles[startColumn - 1];
                styles[startColumn] = lastStyle;
                return;
            }
            resize(line, col + endColumn);
            byte[] styles = this.myLines[line];
            int endIndex = startColumn + endColumn;
            System.arraycopy(styles, startColumn, styles, endIndex, (styles.length - startColumn) - endColumn);
            byte lastStyle = startColumn > 0 ? styles[startColumn - 1] : (byte) 0;
   
            for (int i = startColumn; i < endIndex; i++) {
                styles[i] = lastStyle;
            }
        }
    }
    
    public void resize(int line, int column) {
        resize(line);
        byte[] styles = this.myLines[line];
        if (styles.length <= column) {
            byte lastStyle = styles[styles.length - 1];
            int size = ((column * 5) / 4) + 1;
            byte[] newStyles = new byte[size];
            System.arraycopy(styles, 0, newStyles, 0, styles.length);
            this.myLines[line] = newStyles;
            for (int length = styles.length; length < size; length++) {
                newStyles[length] = lastStyle;
            }
        }
    }
    
    public void resize(int i) {
        if (this.myLines.length <= i) {
            byte[][] styles = new byte[i * 5 / 4][5];
            System.arraycopy(this.myLines, 0, styles, 0, this.myLines.length);
            this.myLines = styles;
        }
    }
    
    public void span(byte style, int startLine, int startColumn, int endLine, int endColumn) {
        if (startLine == endLine) {
            resize(endLine, endColumn + 1);
            byte[] styles = this.myLines[endLine];
            for (int i = startColumn; i <= endColumn; i++) {
                styles[i] = style;
            }
            return;
        }
        resize(startLine, startColumn + 1);
        resize(endLine, endColumn + 1);
        byte[] startStyles = this.myLines[startLine];
        for (int i = startColumn; i < startStyles.length; i++) {
            startStyles[i] = style;
        }
        int firstLine = startLine + 1;
        int line;
        while (true) {
            line = 0;
            if (firstLine >= endLine) {
                break;
            }
            byte[] styles = this.myLines[firstLine];
            while (line < styles.length) {
                styles[line] = style;
                line++;
            }
            firstLine++;
        }
        byte[] endStyles = this.myLines[endLine];
        while (line <= endColumn) {
            endStyles[line] = style;
            line++;
        }
    }
    
    public void clear() {
        for (byte[] bArr : this.myLines) {
            Arrays.fill(bArr, (byte) 0);
        }
    }
    
    public void remove(int startLine, int startColumn, int endLine, int endColumn) {
        if (startLine == endLine) {
            if (startLine < this.myLines.length) {
                byte[] styles = this.myLines[startLine];
                if (endColumn + 1 < styles.length) {
                    System.arraycopy(styles, endColumn + 1, styles, startColumn, (styles.length - endColumn) - 1);
                    return;
                }
                return;
            }
            return;
        }
        if (endLine < this.myLines.length) {
            resize(startLine, getSize(startLine) + getSize(endLine));
            byte[] startStyles = this.myLines[startLine];
            byte[] endStyles = this.myLines[endLine];
            int endIndex = endColumn + 1;
            for (int startIndex = startColumn; endIndex < endStyles.length && startIndex < startStyles.length; startIndex++) {
                startStyles[startIndex] = endStyles[endIndex];
                endIndex++;
            }
        }
        int lastLine = endLine + 1;
        if (lastLine < this.myLines.length) {
            System.arraycopy(this.myLines, lastLine, this.myLines, startLine + 1, (this.myLines.length - endLine) - 2);
            int lineCount = endLine - startLine;
            for (int i = 0; i < lineCount; i++) {
                this.myLines[(this.myLines.length - i) - 1] = new byte[5];
            }
        }
    }
    
    
    public void insert(int startLine, int startColumn, int endLine, int endColumn) {
        if (startLine == endLine) {
            insert(startLine, startColumn, (endColumn - startColumn) + 1);
            return;
        }
        for (int line = startLine; line < endLine; line++) {
            insert(startLine, startColumn);
        }
        insert(endLine, 0, endColumn + 1);
    }
    
    public byte getStyle(int line, int column) {
        if (line >= this.myLines.length) {
            return (byte) 0;
        }
        byte[] styles = this.myLines[line];
        if (column >= styles.length) {
            return styles[styles.length - 1];
        }
        return styles[column];
    }
    
    public void set(int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
        clear();
        for (int i = size - 1; i >= 0; i--) {
            if (styles[i] != -1)
                span((byte) styles[i], startLines[i], startColumns[i], endLines[i], endColumns[i] - 1);
        }
    }
    
    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (byte[] bytes : myLines) {
            builder.append(Arrays.toString(bytes)).append("\n");
        }
        return builder.toString();
    }
}
