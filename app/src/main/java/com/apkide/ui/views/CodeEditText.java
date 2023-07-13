package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.apkide.ui.views.editor.Editor;

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

    public EditorView getEditorView(){
        return (EditorView) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(0,0,r-l,b-t);
    }


    public class EditorView extends Editor{

        public EditorView(Context context) {
            super(context);
        }
    }
}
