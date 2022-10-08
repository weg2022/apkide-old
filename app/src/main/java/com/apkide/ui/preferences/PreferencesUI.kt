package com.apkide.ui.preferences

import android.os.Bundle
import android.view.MenuItem
import com.apkide.ui.R
import com.apkide.ui.StyledUI
import com.apkide.ui.databinding.PreferencesUiBinding
import com.apkide.ui.databinding.PreferencesUiBinding.*

class PreferencesUI : StyledUI() {
	private lateinit var _uiBinding: PreferencesUiBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		_uiBinding = inflate(layoutInflater)
		setContentView(_uiBinding.root)
		setSupportActionBar(_uiBinding.toolbar)
		supportFragmentManager
			.beginTransaction()
			.replace(
				_uiBinding.layout.id,
				PreferenceHeaderFragment()
			)
			.commit()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}
	
	override fun onSupportNavigateUp(): Boolean {
		if (supportFragmentManager.popBackStackImmediate()) {
			return true
		}
		return super.onSupportNavigateUp()
	}
	
	
	@Deprecated("Deprecated in Java")
	override fun onBackPressed() {
		if (supportFragmentManager.backStackEntryCount == 0)
			supportActionBar?.setTitle(R.string.preferences_name)
		super.onBackPressed()
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			android.R.id.home -> {
				if (supportFragmentManager.backStackEntryCount == 0)
					supportActionBar?.setTitle(R.string.preferences_name)
				onBackPressedDispatcher.onBackPressed()
				true
			}
			else -> {
				super.onOptionsItemSelected(item)
			}
		}
	}
	
	
	
}