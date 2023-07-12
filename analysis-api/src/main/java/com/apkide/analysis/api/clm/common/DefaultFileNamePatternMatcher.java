package com.apkide.analysis.api.clm.common;

public class DefaultFileNamePatternMatcher extends FileNamePatternMatcher {
    @Override
    public boolean match(String fileName, String pattern) {
        //*.xxx
        return fileName.toLowerCase().endsWith(pattern.substring(1).toLowerCase());
    }
}
