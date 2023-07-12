package com.apkide.analysis.api.clm.common;

public abstract class FileNamePatternMatcher {

    private static final Object myLock = new Object();

    private static FileNamePatternMatcher sMatcher = new DefaultFileNamePatternMatcher();

    public static void set(FileNamePatternMatcher matcher) {
        synchronized (myLock) {
            sMatcher = matcher;
        }
    }

    public static FileNamePatternMatcher get() {
        synchronized (myLock) {
            return sMatcher;
        }
    }

    public abstract boolean match(String fileName, String pattern);
}
