package com.apkide.ui.views;

import static java.lang.Math.max;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.apkide.ui.views.editor.Editor;
import com.apkide.ui.views.editor.EditorColor;

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

    private String filePath;

    private void initView() {
        removeAllViews();
        addView(new EditorView(getContext()));
    }

    public String getFilePath() {
        return filePath;
    }

    public EditorView getEditorView() {
        return (EditorView) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(0, 0, r - l, b - t);
    }


    public class EditorView extends Editor {
        private final WindowManager windowManager;

        public EditorView(Context context) {
            super(context);
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        @Override
        protected void registerColorScheme() {
            setFontColor(new EditorColor(0xff212121));
            setCaretColor(new EditorColor(0xff212121));
            setCaretLineColor(new EditorColor(0xfff5f5f5));
            setLineNumberColor(new EditorColor(0xffd0d0d0));
            setWhitespaceColor(new EditorColor(0xffd0d0d0));
            setSelectionColor(new EditorColor(0xff0099cc));
            setSelectionBackgroundColor(new EditorColor(0xffa6d2ff));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width;
            int height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                width = windowManager.getCurrentWindowMetrics().getBounds().width();
                height = windowManager.getCurrentWindowMetrics().getBounds().height();
            } else {
                width = windowManager.getDefaultDisplay().getWidth();
                height = windowManager.getDefaultDisplay().getHeight();
            }
            setMeasuredDimension(max(getMeasuredWidth(), width), max(getMeasuredHeight(), height));
        }
    }
}
