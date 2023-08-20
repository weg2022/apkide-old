package com.apkide.editor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class EditorKit extends ViewGroup {
    public EditorKit(Context context) {
        super(context);
        initView();
    }
    
    public EditorKit(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public EditorKit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    private void initView() {
    
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
