package com.apkide.editor;

public interface SelectionListener {
    void selectionChanged(boolean selected,int anchorLine,int anchorColumn,int pointLine,int pointColumn);
}
