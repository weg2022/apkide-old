package com.apkide.ui.browsers.build;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;

public class BuildBrowser extends HeaderBrowserLayout {

	public BuildBrowser(@NonNull Context context) {
		super(context);

		getHeaderIcon().setImageResource(R.drawable.errors_no);
		getHeaderLabel().setText(R.string.browser_label_build);
		getHeaderHelp().setOnClickListener(view -> {

		});
	}

	@NonNull
	@Override
	public String getBrowserName() {
		return "BuildBrowser";
	}
}
