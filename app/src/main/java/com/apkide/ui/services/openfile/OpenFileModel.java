package com.apkide.ui.services.openfile;

import androidx.annotation.NonNull;

import com.apkide.common.LineIterator;
import com.apkide.engine.FileHighlighting;

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

	@NonNull
	String getFilePath();

	long getOpenTimestamps();

	long getLastModified();

	long getFileSize();
}
