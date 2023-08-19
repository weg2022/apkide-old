package com.apkide.ui.preferences;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.apkide.ui.AppPreferences;
import com.apkide.ui.R;

public class AppearancePreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preferences_appearance);
        
        getPreference("app.theme.night").setEnabled(!AppPreferences.isFollowSystemTheme());
        getPreference("app.theme.night").setOnPreferenceClickListener((preference) -> {
            setDefaultNightMode(AppPreferences.isNightTheme() ? MODE_NIGHT_YES : MODE_NIGHT_NO);
            return true;
        });
        
        getPreference("app.theme.followSystem").setOnPreferenceClickListener(preference -> {
            if (AppCompatDelegate.getDefaultNightMode() != MODE_NIGHT_FOLLOW_SYSTEM)
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
            else
                setDefaultNightMode(AppPreferences.isNightTheme() ? MODE_NIGHT_YES : MODE_NIGHT_NO);
            return true;
        });
        
        SwitchPreferenceCompat followSystem = (SwitchPreferenceCompat) getPreference("app.theme.followSystem");
        followSystem.setChecked(AppPreferences.isFollowSystemTheme());
        
        followSystem.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enabled = newValue instanceof Boolean ?
                    (Boolean) newValue :
                    Boolean.parseBoolean(newValue.toString());
            getPreference("app.theme.night").setEnabled(!enabled);
            return true;
        });
    }
    
    private Preference getPreference(CharSequence key) {
        return findPreference(key);
    }
}
