package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.apkide.ui.views.editor.EditorView;

public class CodeEditText extends ViewGroup {
    public CodeEditText(Context context) {
        super(context);
        initView();
    }
    
    public CodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public CodeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        removeAllViews();
        addView(new EditorView(getContext()));
    }
    
    public void insertChar(char c) {
        getEditorView().insertChar(c);
    }
    
    public void focus() {
        if (getEditorView().hasFocus()) return;
        
        getEditorView().requestFocus();
    }
    
    public void showDragHandle(){
        getScrollView().showDragHandle();
    }
    
    
    public int getCaretLine(){
        return getEditorView().getCaretLine();
    }
    
    public int getCaretColumn(){
        return getEditorView().getCaretColumn();
    }
    
    public void select(int startLine, int startColumn, int endLine, int endColumn) {
        getScrollView().selection(startLine, startColumn, endLine, endColumn);
    }
    
    public int getSelectionStartLine() {
        if (getEditorView().isSelectionVisibility()) {
            return getEditorView().getFirstSelectedLine();
        }
        return getEditorView().getCaretLine();
    }
    
    public int getSelectionStartColumn() {
        if (getEditorView().isSelectionVisibility()) {
            return getEditorView().getFirstSelectedColumn();
        }
        return getEditorView().getCaretColumn();
    }
    
    public int getSelectionEndLine() {
        if (getEditorView().isSelectionVisibility()) {
            return getEditorView().getLastSelectedLine();
        }
        return getEditorView().getCaretLine();
    }
    
    public int getSelectionEndColumn() {
        if (getEditorView().isSelectionVisibility()) {
            return getEditorView().getLastSelectedColumn();
        }
        return getEditorView().getCaretColumn();
    }
    
    public int getLineCount() {
        return getEditorView().getEditorModel().getLineCount();
    }
    
    public int getTabSize() {
        return getEditorView().getTabSize();
    }
    
    public CodeEditTextScrollView getScrollView() {
        return (CodeEditTextScrollView) getParent().getParent();
    }
    
    public boolean isTouchEventInsideHandle(MotionEvent event) {
        return getScrollView().isTouchEventInsideHandle(event);
    }
    
    public void setModel(@NonNull CodeEditTextModel model) {
        getEditorView().setModel(model);
    }
    
    @NonNull
    public CodeEditTextModel getCodeEditTextModel() {
        return (CodeEditTextModel) getEditorView().getEditorModel();
    }
    
    @NonNull
    public EditorView getEditorView() {
        return (EditorView) getChildAt(0);
    }
    
    public void redraw(){
        getEditorView().redraw();
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(0, 0, r - l, b - t);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View view = getChildAt(0);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(view.getMeasuredWidth(), view.getMeasuredHeight());
    }
}
