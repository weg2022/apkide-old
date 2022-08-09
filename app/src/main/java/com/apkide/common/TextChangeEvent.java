package com.apkide.common;

import androidx.annotation.NonNull;

import java.util.Objects;

public class TextChangeEvent {
    public int start;
    public String text;
    public int replaceCharCount;
    public int replaceLineCount;
    public int newCharCount;
    public int newLineCount;

    public TextChangeEvent(){

    }

    public TextChangeEvent(int start, @NonNull String text, int replaceCharCount, int replaceLineCount, int newCharCount, int newLineCount) {
        this.start = start;
        this.text = text;
        this.replaceCharCount = replaceCharCount;
        this.replaceLineCount = replaceLineCount;
        this.newCharCount = newCharCount;
        this.newLineCount = newLineCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextChangeEvent that = (TextChangeEvent) o;
        return start == that.start && replaceCharCount == that.replaceCharCount && replaceLineCount == that.replaceLineCount && newCharCount == that.newCharCount && newLineCount == that.newLineCount && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, text, replaceCharCount, replaceLineCount, newCharCount, newLineCount);
    }
}
