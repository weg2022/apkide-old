package com.apkide.ui.services.file;

import androidx.annotation.NonNull;

import com.apkide.codeanalysis.FileHighlights;
import com.apkide.common.io.iterator.LineIterator;

import java.io.IOException;

public interface FileModel {

	@NonNull
	String getFileContent();

	@NonNull
	LineIterator getFileContents();

	void sync() throws IOException;

	void save() throws IOException;

	void highlighting(@NonNull FileHighlights highlighting);

	void semanticHighlighting(@NonNull FileHighlights highlighting);

	boolean isReadOnly();
	
	boolean isBinary();

	@NonNull
	String getFilePath();
	
	long getLastModified();

	long getFileSize();
}
