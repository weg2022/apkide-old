package com.apkide.editor.text;

public interface BufferListener {

    void bufferReset(CharBuffer buffer, char[] buf);

    void bufferChanged(CharBuffer buffer, int index, int length, char[] buf);
}
