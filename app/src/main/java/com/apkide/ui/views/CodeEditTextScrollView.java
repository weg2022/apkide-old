package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

public class CodeEditTextScrollView extends NestedScrollView {
	public CodeEditTextScrollView(Context context) {
		this(context, null);
	}
	
	public CodeEditTextScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CodeEditTextScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
}
