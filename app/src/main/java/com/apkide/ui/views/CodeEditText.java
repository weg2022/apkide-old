package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

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

    private String filePath;

    private void initView() {
        removeAllViews();
        addView(new EditorView(getContext()));
    }

    public String getFilePath() {
        return filePath;
    }

    public EditorView getEditorView() {
        return (EditorView) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(0, 0, r - l, b - t);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View view=getChildAt(0);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(view.getMeasuredWidth(),view.getMeasuredHeight());
    }

}
