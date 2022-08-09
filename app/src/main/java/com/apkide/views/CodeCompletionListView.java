package com.apkide.views;

import android.content.Context;
import android.util.AttributeSet;

public class CodeCompletionListView extends QuickKeyListView{
    public CodeCompletionListView(Context context) {
        super(context);
        initView();
    }

    public CodeCompletionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CodeCompletionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private static final String TAG = "CodeCompletionListView";

    private void initView() {

    }

}
