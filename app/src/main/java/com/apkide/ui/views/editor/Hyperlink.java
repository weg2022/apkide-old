package com.apkide.ui.views.editor;

public class Hyperlink {
    public String url;
    public int startLine,startColumn;
    public int endLine,endColumn;
    
    public Hyperlink(String url, int startLine, int startColumn, int endLine, int endColumn) {
        this.url = url;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }
}
