package com.apkide.ui.browsers.build;

import android.content.Context;
import android.util.AttributeSet;

import com.apkide.ui.views.CodeEditText;

public class OutputView extends CodeEditText {
    public OutputView(Context context) {
        super(context);
        initView();
    }

    public OutputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public OutputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        
    }
}
