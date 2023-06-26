package com.apkide.common.text;

public class TextChangeEvent {

    public int start;
    public String newText;
    public int replaceCharCount;
    public int newCharCount;
    public int replaceLineCount;
    public int newLineCount;

    public void copyTo(TextChangeEvent event) {
        event.start = start;
        event.newText = newText;
        event.replaceCharCount = replaceCharCount;
        event.newCharCount = newCharCount;
        event.replaceLineCount = replaceLineCount;
        event.newLineCount = newLineCount;
    }
}
