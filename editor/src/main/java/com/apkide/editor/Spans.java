package com.apkide.editor;

import static java.lang.System.arraycopy;
import static java.util.Arrays.fill;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Spans {
    private int[][] spans = new int[100][3];
    private int length = 0;

    public void reset() {
        for (int[] highlight : spans) {
            fill(highlight, 0);
        }
        length = 0;
    }


    private void resize() {
        if (spans.length <= length) {
            int[][] temp = new int[length * 5 / 4][5];
            arraycopy(spans, 0, temp, 0, spans.length);
            spans = temp;
        }
    }

    private void sort() {
        Arrays.sort(spans,0,length, (o1, o2) -> {
            if (o1[1] == o2[1]) {
                if (o1[2] == o2[2])
                    return Integer.compare(o1[0], o2[0]);

                return Integer.compare(o1[2], o2[2]);
            }
            return Integer.compare(o1[1], o2[1]);
        });
    }

    public void store(StyleTypes[] styles, int[] starts, int[] ends, int size) {
        reset();
        for (int i = size - 1; i >= 0; i--) {
            span(styles[i].ordinal(), starts[i], ends[i]);
        }
        sort();
    }

    public void insert(int offset,int length){

    }

    public void delete(int offset,int length){

    }


    private void span(int style, int start, int end) {
        spans[length][0] = style;
        spans[length][1] = start;
        spans[length][2] = end;
        length++;
        resize();
    }

    public int length(){
        return length;
    }

    public int style(int index) {
        return spans[index][0];
    }

    public int start(int index) {
        return spans[index][1];
    }

    public int end(int index) {
        return spans[index][2];
    }

    public int findSpanIndex(int offset) {
        return findSpanIndex(0, length, offset);
    }

    public int findSpanIndex(int fromIndex, int toIndex, int offset) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            if (start(mid) > offset) {
                high = mid - 1;
            } else if (end(mid) < offset) {
                low = mid + 1;
            } else
                return mid;
        }
        return -1;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            buffer.append(Arrays.toString(spans[i])).append("\n");
        }
        return buffer.toString();
    }
}
