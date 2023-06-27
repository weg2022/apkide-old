package com.apkide.ui.services.navigate;

import static java.lang.Math.abs;

import java.util.Objects;

public class FileSpan {
    public String filePath;
    public int startLine;
    public int startColumn;
    public int endLine;
    public int endColumn;

    public FileSpan(String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        this.filePath = filePath;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public boolean isNeedUpdate(FileSpan span){
        if (span.filePath.equals(this.filePath)) {
            return abs(span.startLine - this.startLine) < 20;
        }
        return false;

    }

    public boolean isSameLine() {
        return startLine == endLine;
    }

    public boolean isSamePosition() {
        return startLine == endLine && startColumn == endColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSpan fileSpan = (FileSpan) o;
        return startLine == fileSpan.startLine && startColumn == fileSpan.startColumn && endLine == fileSpan.endLine && endColumn == fileSpan.endColumn && Objects.equals(filePath, fileSpan.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, startLine, startColumn, endLine, endColumn);
    }
}
