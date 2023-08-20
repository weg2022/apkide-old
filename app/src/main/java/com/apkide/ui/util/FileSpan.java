package com.apkide.ui.util;

public class FileSpan {
    
    public String filePath;
    public int startLine,startColumn;
    public int endLine,endColumn;
    
    public FileSpan(String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        this.filePath = filePath;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }
}
