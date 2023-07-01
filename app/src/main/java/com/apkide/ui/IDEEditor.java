package com.apkide.ui;

import android.content.Context;
import android.util.AttributeSet;

import io.github.rosemoe.sora.widget.CodeEditor;

public class IDEEditor extends CodeEditor {

    public IDEEditor(Context context) {
        super(context);
        init();
    }

    public IDEEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IDEEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    public void updateConfigure(){

        setTabWidth(AppPreferences.getTabSize());
    }



}
