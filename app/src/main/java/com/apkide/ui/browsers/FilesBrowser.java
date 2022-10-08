package com.apkide.ui.browsers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class FilesBrowser extends LinearLayout implements Browser {
	public FilesBrowser(Context context) {
		this(context,null);
	}
	
	public FilesBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void apply() {
	
	}
	
	@Override
	public void unApply() {
	
	}
}
