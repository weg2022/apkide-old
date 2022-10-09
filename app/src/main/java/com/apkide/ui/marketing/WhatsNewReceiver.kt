package com.apkide.ui.marketing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PackageInfoFlags
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class WhatsNewReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		val preferences = PreferenceManager.getDefaultSharedPreferences(context)
		val prefAppVersionCode = preferences.getLong("app_version_code", -1)
		val appVersionCode: Long = try {
			val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				context.packageManager.getPackageInfo(
					context.packageName,
					PackageInfoFlags.of(0)
				)
			} else {
				context.packageManager.getPackageInfo(context.packageName, 0)
			}
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				info.longVersionCode
			} else {
				info.versionCode.toLong()
			}
		} catch (e: PackageManager.NameNotFoundException) {
			prefAppVersionCode
		}
		if (appVersionCode > prefAppVersionCode) {
			if (context is AppCompatActivity) {
				preferences.edit().putLong("app_version_code", appVersionCode).apply()
				val dialogFragment = WhatsNewDialogFragment()
				dialogFragment.show(
					context.supportFragmentManager,
					WhatsNewReceiver::class.java.simpleName
				)
			}
		}
	}
	
	companion object {
		const val ACTION_WHATS_NEW = "ide.intent.action.whatsnew"
	}
}