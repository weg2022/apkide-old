package com.apkide.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

public class CodeEditTextScrollView extends NestedScrollView {
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

    private static final String TAG = "CodeEditTextScrollView";

    private void initView() {

    }

}
