package com.apkide.common;

public enum LineBreak {
    LF("\n"),
    CR("\r"),
    CRLF("\r\n");
    public final String delimiter;

    LineBreak(String delimiter) {
        this.delimiter = delimiter;
    }
}
