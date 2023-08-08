package com.apkide.ui.browsers.file;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.ui.App;

public class FileBrowserService {

	public interface FileBrowserServiceListener {
		void folderOpened(String folderPath);
	}

	private SharedPreferences myPreferences;
	private FileBrowserServiceListener myListener;


	public void setFileBrowserServiceListener(FileBrowserServiceListener listener) {
		myListener = listener;
	}

	public void folderOpen(@NonNull String folderPath) {
		if (!FileSystem.isDirectory(folderPath)) return;
		getPreferences().edit().putString("open.folder", folderPath).apply();
		if (myListener != null)
			myListener.folderOpened(folderPath);
	}

	@NonNull
	public String getFolderPath() {
		String path = getPreferences().getString("open.folder", null);
		if (path == null || !FileSystem.isDirectory(path)) {
			path = getDefaultFolderPath();
			FileSystem.mkdirs(path);
		}
		return path;
	}

	@NonNull
	public String getDefaultFolderPath() {
		return App.getHomeDir().getAbsolutePath();
	}

	private SharedPreferences getPreferences() {
		if (myPreferences == null)
			myPreferences = App.getPreferences("FileBrowserService");
		return myPreferences;
	}
}
