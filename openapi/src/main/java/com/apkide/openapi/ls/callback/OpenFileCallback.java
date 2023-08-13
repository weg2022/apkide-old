package com.apkide.openapi.ls.callback;

import androidx.annotation.NonNull;

import com.apkide.common.collection.List;

import java.io.Reader;

public interface OpenFileCallback {
	@NonNull
	Reader getOpenFileReader(@NonNull String filePath);

	long getOpenFileVersion(@NonNull String filePath);

	long getOpenFileSize(@NonNull String filePath);

	boolean isOpenFile(@NonNull String filePath);

	@NonNull
	List<String> getOpenFiles();

	void sync();
}
