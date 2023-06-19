package com.apkide.editor;

import com.apkide.common.SafeListenerList;

public class Caret {
    private final EditorView view;
    //private final Vector<Position> positions;

    private final SafeListenerList<CaretListener> caretListeners = new SafeListenerList<>();
    private final SafeListenerList<SelectionListener> selectionListeners = new SafeListenerList<>();
    private final Position position = new Position(0, 0);

    public Caret(EditorView view) {
        this.view = view;
        PositionListener positionListener = new PositionListener() {
            @Override
            public void positionChanged(Position position, boolean anchor, int oldLine, int oldColumn, int line, int column) {
                if (anchor) return;
                for (CaretListener listener : caretListeners) {
                    listener.caretMoved(oldLine, oldColumn, line, column);
                }
            }

            @Override
            public void positionSelectChanged(Position position, boolean selected) {
                for (SelectionListener listener : selectionListeners) {
                    listener.selectionChanged(selected, position.anchorLine, position.anchorColumn, position.line, position.column);
                }
            }
        };
        this.position.addListener(positionListener);
    }

    public void addCaretListener(CaretListener listener){
        caretListeners.add(listener);
    }

    public void removeCaretListener(CaretListener listener){
        caretListeners.remove(listener);
    }

    public void addSelectionListener(SelectionListener listener){
        selectionListeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener listener){
        selectionListeners.remove(listener);
    }

    public void move(int line, int column) {
        position.move(line, column);
    }

    public void setAnchor(int line, int column) {
        position.setAnchor(line, column);
    }

    public void setPoint(int line, int column) {
        position.setPoint(line, column);
    }

    public void cancelSelection() {
        position.cancelSelect();
    }

    public boolean isSelection() {
        return position.isSelected();
    }

    public Position getPosition() {
        return position;
    }

    public int getLine() {
        return position.line;
    }

    public int getColumn() {
        return position.column;
    }

    public int getAnchorLine(){
        return position.anchorLine;
    }

    public int getAnchorColumn(){
        return position.anchorColumn;
    }

    public void resume() {

    }

    public void destroy() {

    }
}
