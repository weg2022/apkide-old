package com.apkide.ui.preferences

import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceHeaderFragmentCompat

class PreferenceHeaderFragment : PreferenceHeaderFragmentCompat() {
	override fun onCreatePreferenceHeader(): PreferenceFragmentCompat {
		return HeadersPreferencesFragment()
	}
	
	override fun onPreferenceStartFragment(
		caller: PreferenceFragmentCompat,
		pref: Preference
	): Boolean {
		if (activity is PreferencesUI) {
			if ((activity as PreferencesUI?)!!.supportActionBar != null) {
				(activity as PreferencesUI?)!!.supportActionBar!!.title = pref.title
			}
		}
		return super.onPreferenceStartFragment(caller, pref)
	}
}