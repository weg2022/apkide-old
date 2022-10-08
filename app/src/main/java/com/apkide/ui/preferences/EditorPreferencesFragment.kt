package com.apkide.ui.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.apkide.ui.R

class EditorPreferencesFragment : PreferenceFragmentCompat() {
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		addPreferencesFromResource(R.xml.editor_preferences)
	}
}