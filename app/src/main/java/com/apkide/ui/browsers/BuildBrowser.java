package com.apkide.ui.browsers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BuildBrowser extends LinearLayout implements Browser{
	public BuildBrowser(Context context) {
		this(context,null);
	}
	
	public BuildBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void apply() {
	
	}
	
	@Override
	public void unApply() {
	
	}
}
