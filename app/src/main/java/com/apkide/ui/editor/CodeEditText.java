package com.apkide.ui.editor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

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

	private void initView() {

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		getChildAt(0).layout(0,0,r-l,b-t);
	}


}
