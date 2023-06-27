package com.apkide.ui.views.editor;

import static java.lang.Integer.compare;
import static java.lang.System.arraycopy;

import java.util.Arrays;
import java.util.Comparator;

public class Spans {
    public static final Spans EMPTY = new Spans(1);

    private int[][] spans;
    private int length;
    private final Comparator<int[]> comparator = (o1, o2) -> {
        if (o1[1] == o2[1]) {
            if (o1[2] == o2[2]) {
                return compare(o1[0], o2[0]);
            }
            return compare(o1[2], o2[2]);
        }
        return compare(o1[1], o2[1]);
    };

    public Spans(int size) {
        spans = new int[Math.max(size, 10)][3];
        length = 0;
    }

    private void sort() {
        Arrays.sort(spans, 0, length, comparator);
    }

    private void put(int[] ids, int[] starts, int[] lengths, int size) {
        for (int i = 0; i < size; i++) {
            put(ids[i], starts[i], lengths[i]);
        }
    }

    private void put(int id, int start, int len) {
        resize();
        spans[length][0] = id;
        spans[length][1] = start;
        spans[length][2] = len;
        length++;
    }

    private void resize() {
        if (spans.length <= length) {
            int[][] temp = new int[length * 5 / 4][3];
            arraycopy(spans, 0, temp, 0, spans.length);
            spans = temp;
        }
    }

    public int getId(int index) {
        return spans[index][0];
    }

    public int getStart(int index) {
        return spans[index][1];
    }

    public int getLength(int index) {
        return spans[index][2];
    }

    public int[] getSpan(int index) {
        return spans[index];
    }

    public int findIndex(int position) {
        return findIndex(0, length, position);
    }

    public int findIndex(int fromIndex, int position) {
        return findIndex(fromIndex, length, position);
    }

    public int findIndex(int fromIndex, int toIndex, int position) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int end = spans[mid][1] + spans[mid][2];

            if (end < position)
                low = mid + 1;
            else if (spans[mid][1] > position)
                high = mid - 1;
            else
                return mid;
        }
        return -1;
    }

    public int length() {
        return length;
    }

    public static class Builder {

        private int[] ids;
        private int[] starts;
        private int[] lengths;
        private int index;

        public Builder() {
            this(100);
        }

        public Builder(int size) {
            size = Math.max(size, 10);
            ids = new int[size];
            starts = new int[size];
            lengths = new int[size];
            index = 0;
        }

        public Builder put(int id, int start, int length) {
            resize();
            ids[index] = id;
            starts[index] = start;
            lengths[index] = length;
            index++;
            return this;
        }


        public Spans build() {
            Spans spans = new Spans(index);
            spans.put(ids, starts, lengths, index);
            spans.sort();
            return spans;
        }

        private void resize() {
            if (ids.length <= index) {
                int size = index * 5 / 4;
                int[] temp = new int[size];
                arraycopy(ids, 0, temp, 0, ids.length);
                ids = temp;

                temp = new int[size];
                arraycopy(starts, 0, temp, 0, starts.length);
                starts = temp;

                temp = new int[size];
                arraycopy(lengths, 0, temp, 0, lengths.length);
                lengths = temp;
            }
        }

    }
}
