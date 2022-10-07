package com.apkide.ui.preferences;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.apkide.ui.R;

public class SourceControlPreferencesFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.sourcecontrol_preferences);
	}
}
