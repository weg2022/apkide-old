package com.apkide.ui.services.file;

import androidx.annotation.NonNull;

import java.io.IOException;

public interface FileModelFactory {

	@NonNull
	String getName();

	boolean isSupportedFile(@NonNull String filePath);

	@NonNull
	FileModel createFileModel(@NonNull String filePath) throws IOException;
}
