package com.apkide.ui.browsers;

import static android.view.LayoutInflater.from;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.databinding.BrowserHeaderBinding;

public abstract class BrowserLayout extends LinearLayout implements Browser {
	private SharedPreferences preferences;
	private final BrowserHeaderBinding myHeaderBinding;

	public BrowserLayout(Context context) {
		super(context);
		removeAllViews();
		setOrientation(VERTICAL);
		LayoutInflater inflater = from(getContext());
		myHeaderBinding = BrowserHeaderBinding.inflate(inflater, this, false);
		addView(myHeaderBinding.getRoot());
	}

	@NonNull
	public final BrowserHeaderBinding getHeader() {
		return myHeaderBinding;
	}

	@NonNull
	public abstract String getPreferencesName();

	@NonNull
	public SharedPreferences getPreferences() {
		if (preferences == null)
			preferences = App.getPreferences(getPreferencesName());
		return preferences;
	}

	@Override
	public void onApply() {
		requestFocus();
	}
}
