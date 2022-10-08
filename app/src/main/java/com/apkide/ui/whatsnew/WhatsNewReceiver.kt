package com.apkide.ui.whatsnew

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class WhatsNewReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		val preferences = PreferenceManager.getDefaultSharedPreferences(context)
		val prefAppVersionCode = preferences.getInt("app_version_code", -1)
		val appVersionCode: Int = try {
			val info = context.packageManager.getPackageInfo(context.packageName, 0)
			info.versionCode
		} catch (e: PackageManager.NameNotFoundException) {
			prefAppVersionCode
		}
		if (appVersionCode>prefAppVersionCode) {
			if (context is AppCompatActivity) {
				preferences.edit().putInt("app_version_code", appVersionCode).apply()
				val dialogFragment = WhatsNewDialogFragment()
				dialogFragment.show(context.supportFragmentManager, TAG)
			}
		}
	}
	
	companion object {
		private const val TAG = "WhatsNew"
		const val ACTION_WHATS_NEW = "ide.intent.action.whatsnew"
	}
}