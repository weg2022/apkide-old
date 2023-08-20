package com.apkide.ls.api.highlighting;

public class Highlights {
    public int[] styles = new int[10000];
    public int[] startLines = new int[10000];
    public int[] startColumns = new int[10000];
    public int[] endLines = new int[10000];
    public int[] endColumns = new int[10000];
    public int size = 0;
    
    
    public void clear() {
        size = 0;
    }
    
    public void highlight(int style, int startLine, int startColumn, int endLine, int endColumn) {
        resize();
        styles[size] = style;
        startLines[size] = startLine;
        startColumns[size] = startColumn;
        endLines[size] = endLine;
        endColumns[size] = endColumn;
        size++;
    }
    
    private void resize() {
        if (size >= styles.length) {
            int newSize = size * 5 / 4;
            int[] newStyles = new int[newSize];
            int[] newStartLines = new int[newSize];
            int[] newStartColumns = new int[newSize];
            int[] newEndLines = new int[newSize];
            int[] newEndColumns = new int[newSize];
            
            System.arraycopy(styles, 0, newStyles, 0, styles.length);
            System.arraycopy(startLines, 0, newStartLines, 0, startLines.length);
            System.arraycopy(startColumns, 0, newStartColumns, 0, startColumns.length);
            System.arraycopy(endLines, 0, newEndLines, 0, endLines.length);
            System.arraycopy(endColumns, 0, newEndColumns, 0, endColumns.length);
            
            styles = newStyles;
            startLines = newStartLines;
            startColumns = newStartColumns;
            endLines = newEndLines;
            endColumns = newEndColumns;
        }
    }
    
}
