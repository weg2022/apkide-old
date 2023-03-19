package com.apkide.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainUI extends ThemeUI implements SharedPreferences.OnSharedPreferenceChangeListener {
	IDEEditor editor;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		App.init(this);
		super.onCreate(savedInstanceState);
		AppPreferences.getPreferences().registerOnSharedPreferenceChangeListener(this);
		IDEEditor editor = new IDEEditor(this);
		setContentView(editor);
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
			changeColorScheme(newConfig.isNightModeActive());
		else
			changeColorScheme(isNightModeActive(newConfig.uiMode));
	}

	private void changeColorScheme(boolean dark) {
		if (editor != null)
			editor.setDarkTheme(dark);
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}
}
