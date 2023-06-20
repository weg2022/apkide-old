package com.apkide.editor;

import android.graphics.Canvas;

public class EditorRenderer {

    private final EditorView view;
    private final EditorPaint paint;
    private final EditorPaint tempPaint;

    private int tabSize = 4;

    public EditorRenderer(EditorView view) {
        this.view = view;
        this.paint = new EditorPaint();
        this.tempPaint = new EditorPaint();
    }


    public void setTabSize(int tabSize) {
        if (this.tabSize == tabSize) return;

        this.tabSize = tabSize;
        paint.setTabSize(tabSize);
    }

    public int getTabSize() {
        return tabSize;
    }

    public int getTabAdvanceInt() {
        return paint.tabAdvanceInt;
    }

    public float getTabAdvance() {
        return paint.tabAdvance;
    }

    public int getSpaceAdvanceInt() {
        return paint.spaceAdvanceInt;
    }

    public float getSpaceAdvance() {
        return paint.spaceAdvance;
    }

    public PaintMetrics getPaintMetrics() {
        return paint.getMetrics();
    }

    public int getLineHeightInt() {
        return paint.getHeightInt();
    }

    public float getLineHeight() {
        return paint.getHeight();
    }


    public void resume() {

    }

    public void destroy() {

    }


    public void redraw() {

    }


    public void draw(Canvas canvas) {

    }
}
