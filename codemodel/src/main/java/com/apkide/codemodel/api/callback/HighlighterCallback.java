package com.apkide.codemodel.api.callback;

import com.apkide.codemodel.api.FileEntry;

public interface HighlighterCallback {
	void fileStarted(FileEntry file);
	
	
	void fileFinished(FileEntry file);
}
