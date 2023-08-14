package com.apkide.ui.browsers.file;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.util.IDEService;

import java.io.File;
import java.io.IOException;

public class FileBrowserService implements IDEService {

	public interface FileBrowserServiceListener {
		void fileBrowserFolderChanged(@NonNull String folderPath);
	}

	private SharedPreferences myPreferences;

	private FileBrowserServiceListener myListener;

	@Override
	public void initialize() {

	}

	@Override
	public void shutdown() {

	}

	public void setListener(FileBrowserServiceListener listener) {
		myListener = listener;
	}

	public void sync() {
		String lastFolder = getCurrentFolder();
		if (myListener != null)
			myListener.fileBrowserFolderChanged(lastFolder);
	}

	public void openFolder(@NonNull String folder) {
		if (FileSystem.isNormalDirectory(folder) && !getCurrentFolder().equals(folder)) {
			getPreferences().edit().putString("open.folder", folder).apply();
			if (myListener != null)
				myListener.fileBrowserFolderChanged(folder);
		}
	}


	@NonNull
	public String getCurrentFolder() {
		return getPreferences().getString("open.folder", getDefaultFolder());
	}

	@NonNull
	public String getDefaultFolder() {
		File file = App.getContext().getExternalFilesDir(null);
		assert file != null;
		if (!file.exists() || !file.isDirectory()) {
			try {
				FileSystem.mkdir(file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file.getAbsolutePath();
	}

	@NonNull
	protected SharedPreferences getPreferences() {
		if (myPreferences == null)
			myPreferences = App.getPreferences("FileBrowserService");
		return myPreferences;
	}
}
