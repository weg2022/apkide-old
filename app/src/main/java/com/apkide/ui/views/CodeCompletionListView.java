package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class CodeCompletionListView extends KeysListView {
	public CodeCompletionListView(Context context) {
		this(context, null);
	}
	
	public CodeCompletionListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CodeCompletionListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
