package com.apkide.ui.services.file;

import androidx.annotation.NonNull;

public interface FileServiceListener {

	void fileOpened(@NonNull String filePath, @NonNull FileModel fileModel);

	void fileClosed(@NonNull String filePath, @NonNull FileModel fileModel);
	
}
