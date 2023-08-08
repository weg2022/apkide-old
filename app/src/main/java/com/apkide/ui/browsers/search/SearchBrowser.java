package com.apkide.ui.browsers.search;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.browsers.BrowserLayout;

public class SearchBrowser extends BrowserLayout {

	public SearchBrowser(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public String getPreferencesName() {
		return "SearchBrowser";
	}
}
