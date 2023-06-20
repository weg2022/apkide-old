package com.apkide.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class EditorView extends View {
    public EditorView(Context context) {
        this(context, null);
    }

    public EditorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private static final String TAG = "EditorView";

    private EditorRenderer renderer;
    private Caret caret;
    private void initView(){

        caret=new Caret(this);
        renderer=new EditorRenderer(this);
    }



    public void onResume(){
        caret.resume();
        renderer.resume();
    }

    public void onDestroy(){
        caret.destroy();
        renderer.destroy();
    }

    public int getCaretLine(){
        return caret.getLine();
    }

    public int getCaretColumn(){
        return caret.getColumn();
    }

    public int getSelectionAnchorLine(){
        return caret.getAnchorLine();
    }

    public int getSelectionAnchorColumn(){
        return caret.getAnchorColumn();
    }

    public int getSelectionPointLine(){
        return getCaretLine();
    }

    public int getSelectionPointColumn(){
        return getCaretColumn();
    }

    public boolean isSelection(){
        return caret.isSelection();
    }

    public void cancelSelection(){
        caret.cancelSelection();
    }

    public void addCaretListener(CaretListener listener){
        caret.addCaretListener(listener);
    }

    public void removeCaretListener(CaretListener listener){
        caret.removeCaretListener(listener);
    }

    public void addSelectionListener(SelectionListener listener){
        caret.addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener){
        caret.removeSelectionListener(listener);
    }

    public void redraw(){
        renderer.redraw();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        renderer.draw(canvas);
    }
}
