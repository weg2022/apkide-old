package com.apkide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.recyclerview.widget.RecyclerView;

public class KeysListView extends RecyclerView {
	public KeysListView(Context context) {
		this(context, null);
	}
	
	public KeysListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public KeysListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private onKeyEventListener _onKeyEventListener;
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean superKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
	public boolean superKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	public interface onKeyEventListener {
		boolean onKeyUp(int keyCode, KeyEvent event);
		
		boolean onKeyDown(int keyCode, KeyEvent event);
	}
}
