package com.apkide.ui.services.openfile;

import androidx.annotation.NonNull;

import java.io.IOException;

public interface OpenFileModelFactory {

	@NonNull
	String getName();

	boolean isSupportedFile(@NonNull String filePath);

	@NonNull
	OpenFileModel createFileModel(@NonNull String filePath) throws IOException;
}
