package com.apkide.openapi.language.api;

import static java.lang.System.arraycopy;

public class Highlights {

    public int[] kinds = new int[1000];
    public int[] startLines = new int[1000];
    public int[] startColumns = new int[1000];
    public int[] endLines = new int[1000];
    public int[] endColumns = new int[1000];
    public int size = 0;


    public void reset() {
        size = 0;
    }

    public void highlight(int kind, int startLine, int startColumn, int endLine, int endColumn) {
        resize();
        kinds[size] = kind;
        startLines[size] = startLine;
        startColumns[size] = startColumn;
        endLines[size] = endLine;
        endColumns[size] = endColumn;
        size++;
    }

    private void resize() {
        if (kinds.length <= size) {
            int[] temp = new int[size * 5 / 4];
            arraycopy(kinds, 0, temp, 0, kinds.length);
            kinds = temp;
            arraycopy(startLines, 0, temp, 0, startLines.length);
            startLines = temp;
            arraycopy(startColumns, 0, temp, 0, startColumns.length);
            startColumns = temp;
            arraycopy(endLines, 0, temp, 0, endLines.length);
            endLines = temp;
            arraycopy(endColumns, 0, temp, 0, endColumns.length);
            endColumns = temp;
        }
    }

}
