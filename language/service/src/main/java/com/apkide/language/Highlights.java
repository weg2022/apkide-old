package com.apkide.language;

class Highlights {
    public static final int SIZE = 100;
    public int[] styles = new int[SIZE];
    public int[] startLines = new int[SIZE];
    public int[] startColumns = new int[SIZE];
    public int[] endLines = new int[SIZE];
    public int[] endColumns = new int[SIZE];
    public int length = 0;
    
    public Highlights() {
    }
    
    
    public void clear() {
        length = 0;
    }
    
    public void token(int style, int startLine, int startColumn, int endLine, int endColumn) {
        resize();
        styles[length] = style;
        startLines[length] = startLine;
        startColumns[length] = startColumn;
        endLines[length] = endLine;
        endColumns[length] = endColumn;
        length++;
    }
    
    private void resize() {
        if (styles.length <= length) {
            int newSize = (length * 5) / 4;
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
    
    
    public int getLength() {
        return length;
    }
    
    
    
    public void highlighting(String filePath,CodeEngine.HighlightingListener listener) {
        int newSize = length;
        
        int[] newStyles = new int[newSize];
        int[] newStartLines = new int[newSize];
        int[] newStartColumns = new int[newSize];
        int[] newEndLines = new int[newSize];
        int[] newEndColumns = new int[newSize];
        
        System.arraycopy(styles, 0, newStyles, 0, newSize);
        System.arraycopy(startLines, 0, newStartLines, 0, newSize);
        System.arraycopy(startColumns, 0, newStartColumns, 0, newSize);
        System.arraycopy(endLines, 0, newEndLines, 0, newSize);
        System.arraycopy(endColumns, 0, newEndColumns, 0, newSize);
        
        listener.highlighting(filePath,newStyles,newStartLines,newStartColumns,newEndLines,newEndColumns,newSize);
    }
    
    public void highlighting2(String filePath,CodeEngine.HighlightingListener listener) {
        int newSize = length;
        
        int[] newStyles = new int[newSize];
        int[] newStartLines = new int[newSize];
        int[] newStartColumns = new int[newSize];
        int[] newEndLines = new int[newSize];
        int[] newEndColumns = new int[newSize];
        
        System.arraycopy(styles, 0, newStyles, 0, newSize);
        System.arraycopy(startLines, 0, newStartLines, 0, newSize);
        System.arraycopy(startColumns, 0, newStartColumns, 0, newSize);
        System.arraycopy(endLines, 0, newEndLines, 0, newSize);
        System.arraycopy(endColumns, 0, newEndColumns, 0, newSize);
        
        listener.semanticHighlighting(filePath,newStyles,newStartLines,newStartColumns,newEndLines,newEndColumns,newSize);
    }
}
