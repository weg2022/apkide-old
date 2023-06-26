package com.apkide.ui.views.editor;

import android.content.Context;
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

    private void initView(){

    }
}
