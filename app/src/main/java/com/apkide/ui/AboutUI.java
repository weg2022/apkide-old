package com.apkide.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.apkide.ui.databinding.UiAboutBinding;

public class AboutUI extends StyledUI {

    private UiAboutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UiAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.aboutToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.aboutToolbar.setNavigationOnClickListener(v -> finish());
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
