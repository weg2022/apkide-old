package com.apkide.ui.services.file;

public interface OpenFileServiceListener {
	
	void fileOpened(OpenFileModel fileModel);
	
	void fileClosed(OpenFileModel fileModel);
}
