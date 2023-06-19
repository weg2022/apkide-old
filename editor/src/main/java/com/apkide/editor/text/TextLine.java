package com.apkide.editor.text;

public interface TextLine extends CharBuffer {

    default void insert(int index, char[] buffer) {
        replace(index, 0, buffer);
    }

    default void delete(int index, int length) {
        replace(index, length, null);
    }

    void setMark(int kind, long mark);

    long getMark(int kid);

    boolean hidden();

    void setVisualLine(int line);

    int getVisualLine();

    int getVisualLength(int tabSize);

    int getVisualWidth();

    void setVisualWidth(int width);

    int getVisualHeight();

    void setVisualHeight(int height);

    int getPhysicalLine();

    void setPhysicalLine(int line);

    default int getPhysicalLength() {
        return getLength();
    }

    int getPhysicalWidth();

    void setPhysicalWidth(int width);

    int getPhysicalHeight();

    void setPhysicalHeight(int height);
}
