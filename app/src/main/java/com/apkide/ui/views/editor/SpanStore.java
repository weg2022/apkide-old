package com.apkide.ui.views.editor;

import static java.lang.Integer.compare;

import com.apkide.common.StoreInputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class SpanStore {

    private final Comparator<int[]> myComparator = (o1, o2) -> {
        if (o1[1] == o2[1]) {
            if (o1[2] == o2[2]) {
                if (o1[3] == o2[3])
                    return compare(o1[4], o2[4]);

                return compare(o1[3], o2[3]);
            }
            return compare(o1[2], o2[2]);
        }
        return compare(o1[1], o2[1]);
    };

    private int[][] mySpans = new int[10000][5];
    private int myCount = 0;


    public void reset() {
        myCount = 0;
    }

    public int getCount() {
        return myCount;
    }

    public boolean isEmpty() {
        return myCount == 0;
    }

    public int getKind(int[] span) {
        return span[0];
    }

    public int getStartLine(int[] span) {
        return span[1];
    }

    public int getStartColumn(int[] span) {
        return span[2];
    }

    public int getEndLine(int[] span) {
        return span[3];
    }

    public int getEndColumn(int[] span) {
        return span[4];
    }

    public void sort() {
        Arrays.sort(mySpans, 0, myCount, myComparator);
    }

    public int[] spanAt(int index) {
        return mySpans[index];
    }

    public int findSpanIndex(int fromIndex, int toIndex, int line, int column) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int[] midVal = mySpans[mid];
            int startLine = getStartLine(midVal);
            int startColumn = getStartColumn(midVal);
            int endLine = getEndLine(midVal);
            int endColumn = getEndColumn(midVal);

            if (startLine > line)
                high = mid - 1;
            else if (endLine < line)
                low = mid + 1;
            else {
                if (startLine == line && startColumn > column)
                    high = mid - 1;
                else if (endLine == line && endColumn < column)
                    low = mid + 1;
                else
                    return mid;
            }
        }
        return -1;
    }


    public void set(int[] kinds, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
        myCount = 0;
        resize(size);
        for (int i = 0; i < size; i++) {
            addSpan(kinds[i], startLines[i], startColumns[i], endLines[i], endColumns[i]);
        }
    }

    private void addSpan(int kind, int startLine, int startColumn, int endLine, int endColumn) {
        mySpans[myCount][0] = kind;
        mySpans[myCount][1] = startLine;
        mySpans[myCount][2] = startColumn;
        mySpans[myCount][3] = endLine;
        mySpans[myCount][4] = endColumn;
        myCount++;
    }

    private void resize(int size) {
        if (mySpans.length <= size) {
            int newSize = (size * 5) / 4;
            int[][] temp = new int[newSize][5];
            System.arraycopy(mySpans, 0, temp, 0, mySpans.length);
            mySpans = temp;
        }
    }

    public void adjustSpanOnInserted(int startLine, int startColumn, int endLine, int endColumn) {

    }

    public void adjustSpanOnRemoved(int startLine, int startColumn, int endLine, int endColumn) {

    }

    public void load(StoreInputStream in) throws IOException {

    }

    public void store(StoreInputStream out) throws IOException {

    }
}
