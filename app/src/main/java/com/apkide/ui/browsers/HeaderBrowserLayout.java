package com.apkide.ui.browsers;

import static android.view.ViewGroup.LayoutParams.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.apkide.ui.databinding.BrowserHeaderBinding;

public abstract class HeaderBrowserLayout extends BrowserLayout {

	private final BrowserHeaderBinding myBinding;

	public HeaderBrowserLayout(@NonNull Context context) {
		super(context);
		setLayoutParams(new ViewGroup.LayoutParams(
				MATCH_PARENT,
				MATCH_PARENT));
		removeAllViews();
		setOrientation(VERTICAL);
		myBinding = BrowserHeaderBinding.inflate(LayoutInflater.from(getContext()), this, false);
		addView(myBinding.getRoot());
	}


	@NonNull
	public TextView getHeaderLabel() {
		return myBinding.browserHeaderLabel;
	}

	@NonNull
	public ImageView getHeaderHelp() {
		return myBinding.browserHeaderHelp;
	}

	@NonNull
	public ImageView getHeaderIcon() {
		return myBinding.browserHeaderIcon;
	}

	@NonNull
	public BrowserHeaderBinding getHeader() {
		return myBinding;
	}
}
