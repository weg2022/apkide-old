package com.apkide.editor;

public interface PositionListener {

    void positionChanged(Position position, boolean anchor,int oldLine,int oldColumn, int line, int column);

    void positionSelectChanged(Position position,boolean selected);
}
