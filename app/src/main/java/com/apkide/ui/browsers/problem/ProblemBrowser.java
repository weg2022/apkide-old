package com.apkide.ui.browsers.problem;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.browsers.BrowserLayout;

public class ProblemBrowser extends BrowserLayout {

	public ProblemBrowser(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public String getPreferencesName() {
		return "ProblemBrowser";
	}
}
