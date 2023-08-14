package com.apkide.ui.services.openfile;

import androidx.annotation.NonNull;

public interface OpenFileServiceListener {

	void fileOpened(@NonNull String filePath, @NonNull OpenFileModel fileModel);

	void fileClosed(@NonNull String filePath, @NonNull OpenFileModel fileModel);
}
