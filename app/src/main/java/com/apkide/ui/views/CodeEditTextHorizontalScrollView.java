package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class CodeEditTextHorizontalScrollView extends HorizontalScrollView {
	public CodeEditTextHorizontalScrollView(Context context) {
		this(context, null);
	}
	
	public CodeEditTextHorizontalScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CodeEditTextHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
