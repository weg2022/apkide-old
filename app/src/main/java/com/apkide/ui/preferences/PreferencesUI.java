package com.apkide.ui.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.apkide.ui.StyledUI;
import com.apkide.ui.databinding.PreferencesUiBinding;

public class PreferencesUI extends StyledUI {
	private PreferencesUiBinding _uiBinding;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_uiBinding = PreferencesUiBinding.inflate(getLayoutInflater());
		setContentView(_uiBinding);
		setSupportActionBar(_uiBinding.toolbar);
		if (getSupportActionBar()!=null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						_uiBinding.layout.getId(),
						new HeadersPreferencesFragment())
				.commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (_uiBinding != null) {
			_uiBinding = null;
		}
	}

}
