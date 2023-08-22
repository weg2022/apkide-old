package com.apkide.ui.services.file;

import androidx.annotation.NonNull;

import com.apkide.common.io.iterator.LineIterator;
import com.apkide.language.FileHighlighting;

import java.io.IOException;

public interface FileModel {
	@NonNull
	String getFileContent();

	@NonNull
	LineIterator getFileContents();

	void sync() throws IOException;

	void save() throws IOException;

	void highlighting(@NonNull FileHighlighting highlighting);

	void semanticHighlighting(@NonNull FileHighlighting highlighting);

	boolean isReadOnly();
	
	boolean isBinary();

	@NonNull
	String getFilePath();
	
	long getLastModified();

	long getFileSize();
}
