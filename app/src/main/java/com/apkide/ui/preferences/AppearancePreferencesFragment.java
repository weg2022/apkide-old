package com.apkide.ui.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.apkide.ui.AppPreferences;
import com.apkide.ui.R;

public class AppearancePreferencesFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
		addPreferencesFromResource(R.xml.preferences_appearance);

	getPreference("app.theme.night").setEnabled(!AppPreferences.isFollowSystemTheme());

	getPreference("app.theme.followSystem").setOnPreferenceChangeListener((preference, newValue) -> {
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
