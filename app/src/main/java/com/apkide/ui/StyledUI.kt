package com.apkide.ui

import android.app.UiModeManager
import android.os.Bundle
import android.view.View
import android.view.View.OnLongClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding

abstract class StyledUI : AppCompatActivity(), View.OnClickListener, OnLongClickListener {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		applyTheme()
	}
	
	private fun applyTheme() {
		val controller = WindowInsetsControllerCompat(window, window.decorView)
		val darkMode =
			(getSystemService(UI_MODE_SERVICE) as UiModeManager).nightMode == UiModeManager.MODE_NIGHT_YES
		controller.isAppearanceLightStatusBars = !darkMode
		controller.isAppearanceLightNavigationBars = !darkMode
	}
	
	fun setContentView(view: ViewBinding) {
		super.setContentView(view.root)
	}
	
	override fun onClick(v: View) {}
	override fun onLongClick(v: View): Boolean {
		return false
	}
}