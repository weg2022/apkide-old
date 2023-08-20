package com.apkide.common;

import androidx.annotation.NonNull;

import com.apkide.common.io.FileUtils;

public class FilePatternMatcher {
    
    private static final Object myLock = new Object();
    
    private static FilePatternMatcher sMatcher = new FilePatternMatcher();
    
    public static void set(@NonNull FilePatternMatcher matcher) {
        synchronized (myLock) {
            sMatcher = matcher;
        }
    }
    
    @NonNull
    public static FilePatternMatcher get() {
        synchronized (myLock) {
            return sMatcher;
        }
    }
    
    public boolean match(@NonNull String filePath, @NonNull String pattern) {
        String name = FileUtils.getFileName(filePath).toLowerCase();
        if (pattern.startsWith("*.")) {
            return name.endsWith(pattern.substring(1).toLowerCase());
        }
        return name.equals(pattern.toLowerCase()) ||
                name.endsWith(pattern.toLowerCase());
    }
}
