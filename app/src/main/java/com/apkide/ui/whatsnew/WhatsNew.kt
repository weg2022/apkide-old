package com.apkide.ui.whatsnew

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity

object WhatsNew {
	private var sWhatsNewReceiver: WhatsNewReceiver? = null
	@JvmStatic
	fun register(activity: AppCompatActivity) {
		sWhatsNewReceiver = WhatsNewReceiver()
		val filter = IntentFilter()
		filter.addAction(WhatsNewReceiver.ACTION_WHATS_NEW)
		activity.registerReceiver(sWhatsNewReceiver, filter)
		val intent = Intent()
		intent.action = WhatsNewReceiver.ACTION_WHATS_NEW
		activity.sendBroadcast(intent)
	}
	
	@JvmStatic
	fun unregister(activity: AppCompatActivity) {
		if (sWhatsNewReceiver != null) {
			activity.unregisterReceiver(sWhatsNewReceiver)
		}
	}
}