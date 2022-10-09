package com.apkide.component.editor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

public class EditorView extends View implements ViewTreeObserver.OnPreDrawListener {
	public EditorView(Context context) {
		this(context, null);
	}
	
	public EditorView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public EditorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	@Override
	public boolean onPreDraw() {
		return false;
	}
}
