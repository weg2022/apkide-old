package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.apkide.ui.views.editor.CaretListener;
import com.apkide.ui.views.editor.Console;
import com.apkide.ui.views.editor.SelectionListener;

public class CodeEditTextScrollView extends ScrollView {
    public CodeEditTextScrollView(Context context) {
        super(context);
        initView();
    }

    public CodeEditTextScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CodeEditTextScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        
    }


    protected HorizontalScrollView getHorizontalScrollView(){
        return (HorizontalScrollView) getChildAt(0);
    }

    protected CodeEditText getCodeEditText(){
        return (CodeEditText) getHorizontalScrollView().getChildAt(0);
    }

    protected CodeEditText.EditorView getEditorView(){
        return getCodeEditText().getEditorView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getEditorView().addCaretListener(new CaretListener() {
            @Override
            public void caretMove(@NonNull Console console, int caretLine, int caretColumn, boolean isTyping) {

            }
        });

        getEditorView().addSelectionListener(new SelectionListener() {
            @Override
            public void selectUpdate(@NonNull Console console) {

            }

            @Override
            public void selectChanged(@NonNull Console console, boolean select) {

            }
        });
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
