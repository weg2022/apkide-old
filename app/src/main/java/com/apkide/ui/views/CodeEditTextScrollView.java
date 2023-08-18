package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.apkide.ui.views.editor.EditorView;

public class CodeEditTextScrollView extends ScrollView {
    public CodeEditTextScrollView(Context context) {
        super(context);
        initView();
    }
    
    public CodeEditTextScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public CodeEditTextScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    private void initView() {
        
    }
    
    private HorizontalScrollView getHorizontalScrollView(){
        return (HorizontalScrollView) getChildAt(0);
    }
    
    private CodeEditText getEditText(){
        return (CodeEditText) getHorizontalScrollView().getChildAt(0);
    }
    
    private EditorView getEditorView(){
        return getEditText().getEditorView();
    }
    
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getEditorView().redraw();
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        getEditorView().redraw();
    }
}
