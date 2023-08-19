package com.apkide.ui.services.openfile;

import androidx.annotation.NonNull;

import com.apkide.common.LineIterator;
import com.apkide.codeanalysis.FileHighlighting;

import java.io.IOException;

public interface OpenFileModel {

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
