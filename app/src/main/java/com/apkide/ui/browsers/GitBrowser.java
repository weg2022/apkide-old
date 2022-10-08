package com.apkide.ui.browsers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class GitBrowser extends LinearLayout implements Browser {
	public GitBrowser(Context context) {
		this(context,null);
	}
	
	public GitBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void apply() {
	
	}
	
	@Override
	public void unApply() {
	
	}
}
