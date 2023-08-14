package com.apkide.ui.browsers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.apkide.ui.App;


public abstract class BrowserLayout extends LinearLayoutCompat implements Browser, ViewModelStoreOwner {

	private SharedPreferences myPreferences;
	private final ViewModelStore myViewModelStore;
	public BrowserLayout(@NonNull Context context) {
		super(context);
		myViewModelStore=new ViewModelStore();
	}


	@NonNull
	public SharedPreferences getPreferences(){
		if (myPreferences==null)
			myPreferences= App.getPreferences(getBrowserName());
		return myPreferences;
	}

	@NonNull
	public abstract String getBrowserName();

	@Override
	public void refresh() {
		requestFocus();
	}

	@NonNull
	@Override
	public ViewModelStore getViewModelStore() {
		return myViewModelStore;
	}

	@Override
	protected void onDetachedFromWindow() {
		myViewModelStore.clear();
		super.onDetachedFromWindow();
	}
}
