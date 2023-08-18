package com.apkide.ui.browsers.build;

import static android.view.LayoutInflater.from;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.databinding.BrowserBuildBinding;

public class BuildBrowser extends HeaderBrowserLayout {

	private BrowserBuildBinding myBinding;
	public BuildBrowser(@NonNull Context context) {
		super(context);
		myBinding=BrowserBuildBinding.inflate(from(getContext()), this,false);
		addView(myBinding.getRoot());
		
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
