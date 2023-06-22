package com.apkide.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.LogListener;
import com.apkide.common.Logger;

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
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
			changeTheme(newConfig.isNightModeActive());
		else
			changeTheme(isNightModeActive(newConfig.uiMode));
	}

	private void changeTheme(boolean dark){

	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}
}
