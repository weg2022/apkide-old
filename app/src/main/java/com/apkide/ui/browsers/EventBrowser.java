package com.apkide.ui.browsers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class EventBrowser extends LinearLayout implements Browser{
	public EventBrowser(Context context) {
		this(context,null);
	}
	
	public EventBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void apply() {
	
	}
	
	@Override
	public void unApply() {
	
	}
}
