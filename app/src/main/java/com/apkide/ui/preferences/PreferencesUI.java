package com.apkide.ui.preferences;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.apkide.ui.R;
import com.apkide.ui.ThemeUI;
import com.apkide.ui.databinding.UiPreferencesBinding;

public class PreferencesUI extends ThemeUI {

    private UiPreferencesBinding preferencesBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesBinding = UiPreferencesBinding.inflate(getLayoutInflater());
        setContentView(preferencesBinding.getRoot());

        setSupportActionBar(preferencesBinding.preferencesToolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferencesBinding.preferencesToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferencesFrame, new HeadersPreferencesFragment())
                .commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preferencesBinding != null)
            preferencesBinding = null;
    }
}
