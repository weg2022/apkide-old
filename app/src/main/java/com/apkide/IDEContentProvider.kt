package com.apkide

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.apkide.core.Core

class IDEContentProvider : ContentProvider() {
	override fun onCreate(): Boolean {
		Core.setContext(context)
		return false
	}
	
	override fun query(
		uri: Uri,
		projection: Array<String>?,
		selection: String?,
		selectionArgs: Array<String>?,
		sortOrder: String?
	): Cursor? {
		return null
	}
	
	override fun getType(uri: Uri): String? {
		return null
	}
	
	override fun insert(uri: Uri, values: ContentValues?): Uri? {
		return null
	}
	
	override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
		return 0
	}
	
	override fun update(
		uri: Uri,
		values: ContentValues?,
		selection: String?,
		selectionArgs: Array<String>?
	): Int {
		return 0
	}
}