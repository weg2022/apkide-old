package com.apkide.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.apkide.ui.views.CodeEditText;

public class IDEEditor extends CodeEditText {
    public IDEEditor(Context context) {
        super(context);
        initView();
    }

    public IDEEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public IDEEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        
    }
}
