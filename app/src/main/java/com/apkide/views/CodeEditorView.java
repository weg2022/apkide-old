package com.apkide.views;

import android.content.Context;
import android.util.AttributeSet;

import com.apkide.views.editor.Editor;

public class CodeEditorView extends Editor {
    public CodeEditorView(Context context) {
        super(context);
        initView();
    }

    public CodeEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CodeEditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private static final String TAG = "CodeEditorView";

    private void initView() {

    }

}
