package com.apkide.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class MainUI extends ThemeUI implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		App.init(this);
		super.onCreate(savedInstanceState);
		AppPreferences.getPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppPreferences.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	
	}
}
