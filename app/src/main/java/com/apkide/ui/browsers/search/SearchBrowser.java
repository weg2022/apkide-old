package com.apkide.ui.browsers.search;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;

public class SearchBrowser extends HeaderBrowserLayout {
	public SearchBrowser(@NonNull Context context) {
		super(context);

		getHeaderIcon().setImageResource(R.drawable.search);
		getHeaderLabel().setText(R.string.browser_label_search);
		getHeaderHelp().setOnClickListener(view -> {

		});
	}

	@NonNull
	@Override
	public String getBrowserName() {
		return "SearchBrowser";
	}
}
