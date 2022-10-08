package com.apkide.ui.whatsnew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.apkide.ui.databinding.WhatsnewDialogBinding
import org.apache.commons.io.IOUtils
import java.io.IOException
import java.io.InputStreamReader

class WhatsNewDialogFragment : DialogFragment() {
	private var _binding: WhatsnewDialogBinding? = null
	private val changes: String
		get() = if (context == null) "" else try {
			IOUtils.toString(InputStreamReader(requireContext().assets.open("whatsnew/apkide-changes.txt")))
		} catch (e: IOException) {
			e.localizedMessage
		}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (dialog != null) {
			dialog!!.window.setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT
			)
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = WhatsnewDialogBinding.inflate(inflater, container, false)
		_binding!!.changesContent.text = changes
		_binding!!.ok.setOnClickListener { if (showsDialog && dialog != null) dialog!!.dismiss() }
		return _binding!!.root
	}
}