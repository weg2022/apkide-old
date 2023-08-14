package com.apkide.ui.browsers.error;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;

public class ErrorBrowser extends HeaderBrowserLayout {
	public ErrorBrowser(@NonNull Context context) {
		super(context);

		getHeaderIcon().setImageResource(R.drawable.errors_no);
		getHeaderLabel().setText(R.string.browser_label_error);
		getHeaderHelp().setOnClickListener(view -> {

		});
	}

	@NonNull
	@Override
	public String getBrowserName() {
		return "ErrorBrowser";
	}
}
