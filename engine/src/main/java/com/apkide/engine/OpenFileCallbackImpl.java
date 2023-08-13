package com.apkide.engine;

import androidx.annotation.NonNull;

import com.apkide.common.collection.List;
import com.apkide.openapi.ls.callback.OpenFileCallback;

import java.io.Reader;

public class OpenFileCallbackImpl implements OpenFileCallback {



	public void openFile(String filePath) {


	}

	public void closeFile(String filePath) {

	}

	@NonNull
	@Override
	public Reader getOpenFileReader(@NonNull String filePath) {
		return null;
	}

	@Override
	public long getOpenFileVersion(@NonNull String filePath) {
		return 0;
	}

	@Override
	public long getOpenFileSize(@NonNull String filePath) {
		return 0;
	}

	@Override
	public boolean isOpenFile(@NonNull String filePath) {
		return false;
	}

	@NonNull
	@Override
	public List<String> getOpenFiles() {
		return null;
	}

	@Override
	public void sync() {


	}
}
