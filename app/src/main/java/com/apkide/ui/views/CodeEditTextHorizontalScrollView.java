package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class CodeEditTextHorizontalScrollView extends HorizontalScrollView {
    public CodeEditTextHorizontalScrollView(Context context) {
        super(context);
    }

    public CodeEditTextHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CodeEditTextHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private CodeEditText getCodeEditText(){
        return (CodeEditText) getChildAt(0);
    }

    private CodeEditText.EditorView getEditorView(){
        return getCodeEditText().getEditorView();
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
