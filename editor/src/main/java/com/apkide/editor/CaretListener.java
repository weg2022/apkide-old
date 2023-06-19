package com.apkide.editor;

public interface CaretListener {
    void caretMoved(int oldCaretLine,int oldCaretColumn,int caretLine,int caretColumn);
}
