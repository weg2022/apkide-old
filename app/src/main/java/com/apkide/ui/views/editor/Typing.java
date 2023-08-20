package com.apkide.ui.views.editor;

public class Typing {
    
    public int line;
    public int startColumn;
    public int endColumn;
    
    public Typing(int line, int startColumn, int endColumn) {
        this.line = line;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }
}
