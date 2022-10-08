package com.apkide.ui.browsers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LogcatBrowser extends LinearLayout implements Browser {
	public LogcatBrowser(Context context) {
		this(context,null);
	}
	
	public LogcatBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void apply() {
	
	}
	
	@Override
	public void unApply() {
	
	}
}
