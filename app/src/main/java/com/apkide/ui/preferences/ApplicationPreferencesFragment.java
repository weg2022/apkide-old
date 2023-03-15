package com.apkide.ui.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.apkide.ui.R;

public class ApplicationPreferencesFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
		addPreferencesFromResource(R.xml.preferences_application);
	}
}