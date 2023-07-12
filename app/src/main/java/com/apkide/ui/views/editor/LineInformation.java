package com.apkide.ui.views.editor;

public class LineInformation {

    public int offset;
    public int length;
    public String delimiter;

    public LineInformation(int offset, int length, String delimiter) {
        this.offset = offset;
        this.length = length;
        this.delimiter = delimiter;
    }
}
