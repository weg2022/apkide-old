package com.apkide.ui.browsers.build;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.browsers.BrowserLayout;

public class BuildBrowser extends BrowserLayout {
	public BuildBrowser(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public String getPreferencesName() {
		return "BuildBrowser";
	}
}
