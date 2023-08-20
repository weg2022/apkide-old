package com.apkide.editor;

import android.content.Context;
import android.util.AttributeSet;

import com.apkide.editor.text.TextField;

public class EditorView extends TextField {
    public EditorView(Context context) {
        super(context);
        initView();
    }
    
    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public EditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    private void initView() {
        
    }
}
