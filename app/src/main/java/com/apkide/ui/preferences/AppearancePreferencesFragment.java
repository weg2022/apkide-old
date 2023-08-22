package com.apkide.ui.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.apkide.ui.AppPreferences;
import com.apkide.ui.R;

public class AppearancePreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preferences_appearance);
        findPreference("app.theme.night").setEnabled(!AppPreferences.isFollowSystemTheme());
        findPreference("app.theme.followSystem").setOnPreferenceChangeListener((preference, newValue) -> {
            findPreference("app.theme.night").setEnabled(Boolean.parseBoolean(newValue.toString()));
            return true;
        });
    }
    
}
