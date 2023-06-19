package com.apkide.editor;

import com.apkide.common.SafeListenerList;

public class Position {
    public int line;
    public int column;
    public int anchorLine;
    public int anchorColumn;

    private final SafeListenerList<PositionListener> listeners;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
        this.anchorLine = line;
        this.anchorColumn = column;
        this.listeners = new SafeListenerList<>();
    }

    public void addListener(PositionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PositionListener listener) {
        listeners.remove(listener);
    }

    public void move(int line, int column) {
        setPoint(line, column);
        setAnchor(line, column);
    }

    public void setAnchor(int line, int column) {
        boolean select = isSelected();
        int oldLine=anchorLine;
        int oldColumn=anchorColumn;
        this.anchorLine = line;
        this.anchorColumn = column;
        for (PositionListener listener : listeners) {
            listener.positionChanged(this, true,oldLine,oldColumn, line, column);
        }

        if (select != isSelected()) {
            for (PositionListener listener : listeners) {
                listener.positionSelectChanged(this, isSelected());
            }
        }
    }

    public void setPoint(int line, int column) {
        boolean select = isSelected();
        int oldLine=this.line;
        int oldColumn=this.column;
        this.line = line;
        this.column = column;
        for (PositionListener listener : listeners) {
            listener.positionChanged(this, false,oldLine,oldColumn, line, column);
        }

        if (select != isSelected()) {
            for (PositionListener listener : listeners) {
                listener.positionSelectChanged(this, isSelected());
            }
        }
    }


    public void cancelSelect() {
        if (!isSelected()) {
            anchorLine = line;
            anchorColumn = column;
            for (PositionListener listener : listeners) {
                listener.positionSelectChanged(this, isSelected());
            }
        }
    }

    public boolean isSelected() {
        return anchorLine != line && anchorColumn != column;
    }
}
