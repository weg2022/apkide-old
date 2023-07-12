package com.apkide.ui.views.editor;

import android.text.GetChars;

import androidx.annotation.NonNull;

public class Model implements CharSequence, GetChars {

    private char[] myChars;
    private int myGapStartIndex;
    private int myGapStopIndex;
    private LineManger myLineManger;


    @Override
    public int length() {
        return getLength();
    }

    @Override
    public char charAt(int index) {
        return myChars[index < myGapStartIndex ? index : index + getGapLength()];
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }

    public int getLength() {
        return myChars.length - getGapLength();
    }

    private int getGapLength() {
        return myGapStopIndex - myGapStartIndex;
    }

    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {

    }

    public int getLineCount() {
        return myLineManger.getLineCount();
    }

    public int getLineStart(int line) {
        return myLineManger.getLineStart(line);
    }

    public int getLineLength(int line) {
        return myLineManger.getLineLength(line);
    }

    public String getLineDelimiter(int line) {
        return myLineManger.getLineDelimiter(line);
    }

}
