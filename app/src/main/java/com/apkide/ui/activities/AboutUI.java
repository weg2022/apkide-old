package com.apkide.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.apkide.ui.ThemeUI;
import com.apkide.ui.databinding.UiAboutBinding;

public class AboutUI extends ThemeUI {

	private UiAboutBinding binding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = UiAboutBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		binding.toolbar.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == android.R.id.home) {
				finish();
				return true;
			}
			return false;
		});
		setSupportActionBar(binding.toolbar);
		assert getSupportActionBar() != null;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		binding.toolbar.setNavigationOnClickListener(v -> finish());
		binding.webView.loadUrl("file:///android_asset/license.html");
		binding.webView.getSettings().setSupportZoom(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (binding != null)
			binding = null;
	}


}
