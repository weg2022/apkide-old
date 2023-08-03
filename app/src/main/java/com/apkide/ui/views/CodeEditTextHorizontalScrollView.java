package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class CodeEditTextHorizontalScrollView extends HorizontalScrollView {
    public CodeEditTextHorizontalScrollView(Context context) {
        super(context);
        initView();
    }

    public CodeEditTextHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CodeEditTextHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {

    }


    protected CodeEditText getCodeEditText() {
        return (CodeEditText) getChildAt(0);
    }

    protected EditorView getEditorView() {
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

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MOVE) != 0) {
            return false;
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isMouseButtonEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isMouseButtonEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_CANCEL ||
                event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getButtonState() == 0) {
                return true;
            }
        }

        if (event.getToolType(0) == 3) {
            return true;
        }
        switch (event.getSource()) {
            case 8194:
            case 1048584:
                return true;
            default:
                return false;
        }
    }
}