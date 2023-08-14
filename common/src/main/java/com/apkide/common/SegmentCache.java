package com.apkide.common;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SegmentCache {
    private static final SegmentCache sharedCache = new SegmentCache();

    private final List<Segment> segments;

    @NonNull
    public static SegmentCache getInstance() {
        return sharedCache;
    }

    @NonNull
    public static Segment getSharedSegment() {
        return getInstance().getSegment();
    }

    public static void releaseSharedSegment(@NonNull Segment segment) {
        getInstance().releaseSegment(segment);
    }

    public SegmentCache() {
        segments = new ArrayList<>(11);
    }

    @NonNull
    public Segment getSegment() {
        synchronized(this) {
            int size = segments.size();

            if (size > 0) {
                return segments.remove(size - 1);
            }
        }
        return new CachedSegment();
    }

    public void releaseSegment(@NonNull Segment segment) {
        if (segment instanceof CachedSegment) {
            synchronized(this) {
                if (segment.copy) {
                    Arrays.fill(segment.array, '\u0000');
                }
                segment.array = null;
                segment.copy = false;
                segment.count = 0;
                segments.add(segment);
            }
        }
    }


    private static class CachedSegment extends Segment {
    }
}
