package com.apkide.common;

import androidx.annotation.NonNull;

public class TextUtilities {
    public static long packRange(int start, int end) {
        return (((long) start) << 32) | end;
    }

    public static int unpackRangeStart(long range) {
        return (int) (range >>> 32);
    }

    public static int unpackRangeEnd(long range) {
        return (int) (range & 0x00000000FFFFFFFFL);
    }

    private static char[] sTemp = null;
    private static final Object sLock = new Object();

    static char[] obtain(int len) {
        char[] buf;

        synchronized (sLock) {
            buf = sTemp;
            sTemp = null;
        }

        if (buf == null || buf.length < len)
            buf = new char[len];
        return buf;
    }

    static void recycle(char[] temp) {
        if (temp.length > 1000)
            return;

        synchronized (sLock) {
            sTemp = temp;
        }
    }

    public static int lineCount(@NonNull String text) {
        int lineCount = 0;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                lineCount++;
            } else if (c == '\r') {
                if (i + 1 < length && text.charAt(i + 1) == '\n')
                    i++;
                lineCount++;
            }
        }
        return lineCount;
    }

    public static int getLineCount(@NonNull Document document, int start, int length) {
        int line = 1;
        int end = start + length;
        if (end > document.getCharCount())
            end = document.getCharCount();

        for (int i = start; i < end; i++) {
            char c = document.charAt(i);
            if (c == '\n') {
                line++;
            } else if (c == '\r') {
                if (i + 1 < end && document.charAt(i + 1) == '\n') {
                    i++;
                }
                line++;
            }
        }
        return line;
    }
}
