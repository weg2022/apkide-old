package com.apkide.common;


import static com.apkide.common.TextUtilities.*;
import static java.lang.Integer.*;

class DocumentCache {
    private static final int CACHE_SIZE = 4;
    private final long[] caches = new long[CACHE_SIZE];

    public DocumentCache() {
        caches[0] = packRange(0, 0);
        for (int i = 1; i < CACHE_SIZE; i++) {
            caches[i] = packRange(-1, -1);
        }
    }

    public long getNearestLine(int line) {
        int nearestMatch = 0;
        int nearestDistance = MAX_VALUE;
        for (int i = 0; i < CACHE_SIZE; i++) {
            int distance = Math.abs(line - unpackRangeStart(caches[i]));
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestMatch = i;
            }
        }
        long nearestEntry = caches[nearestMatch];
        top(nearestMatch);
        return nearestEntry;
    }

    public long getNearestOffset(int offset) {
        int nearestMatch = 0;
        int nearestDistance = MAX_VALUE;
        for (int i = 0; i < CACHE_SIZE; i++) {
            int distance = Math.abs(offset - unpackRangeEnd(caches[i]));
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestMatch = i;
            }
        }
        long nearestEntry = caches[nearestMatch];
        top(nearestMatch);
        return nearestEntry;
    }

    private void top(int index) {
        if (index == 0) return;
        long temp = caches[index];
        System.arraycopy(caches, 1, caches, 2, index - 1);
        caches[1] = temp;
    }

    public void update(int line, int offset) {
        if (line <= 0) return;
        if (!replace(line, offset))
            insert(line, offset);
    }

    private boolean replace(int line, int offset) {
        for (int i = 1; i < CACHE_SIZE; ++i) {
            if (unpackRangeStart(caches[i]) == line) {
                caches[i] = packRange(line, offset);
                return true;
            }
        }
        return false;
    }

    private void insert(int line, int offset) {
        top(CACHE_SIZE - 1);
        caches[1] = packRange(line, offset);
    }


    public void invalidate() {
        for (int i = 0; i < CACHE_SIZE; i++) {
            caches[i] = packRange(-1, -1);
        }
    }

    public void invalidateFrom(int offset) {
        for (int i = 1; i < CACHE_SIZE; i++) {
            if (unpackRangeEnd(caches[i]) >= offset) {
                caches[i] = packRange(-1, -1);
            }
        }
    }
}

