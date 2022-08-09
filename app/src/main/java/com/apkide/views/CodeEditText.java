package com.apkide.views;

import android.content.Context;
import android.util.AttributeSet;
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

    private static final String TAG = "CodeEditText";

    private void initView() {
        removeAllViews();
        addView(new CodeEditorView(getContext()));
    }

    public CodeEditorView getEditorView(){
        return (CodeEditorView) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getChildAt(0).measure(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(getChildAt(0).getMeasuredWidth(),getChildAt(0).getMeasuredHeight());
    }
}
