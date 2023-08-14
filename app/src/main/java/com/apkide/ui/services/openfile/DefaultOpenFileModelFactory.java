package com.apkide.ui.services.openfile;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.common.LineIterator;
import com.apkide.engine.FileHighlighting;

import java.io.IOException;

/**
 * For Tests
 */
public class DefaultOpenFileModelFactory implements OpenFileModelFactory {
	@NonNull
	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public boolean isSupportedFile(@NonNull String filePath) {
		return !FileSystem.isBinary(filePath);
	}

	@NonNull
	@Override
	public OpenFileModel createFileModel(@NonNull String filePath) throws IOException {
		return new OpenFileModel() {
			@NonNull
			@Override
			public String getFileContent() {
				return null;
			}

			@NonNull
			@Override
			public LineIterator getFileContents() {
				return null;
			}

			@Override
			public void sync() throws IOException {

			}

			@Override
			public void save() throws IOException {

			}

			@Override
			public void highlighting(@NonNull FileHighlighting highlighting) {

			}

			@Override
			public void semanticHighlighting(@NonNull FileHighlighting highlighting) {

			}

			@Override
			public boolean isReadOnly() {
				return false;
			}

			@NonNull
			@Override
			public String getFilePath() {
				return filePath;
			}

			@Override
			public long getOpenTimestamps() {
				return 0;
			}

			@Override
			public long getLastModified() {
				return 0;
			}

			@Override
			public long getFileSize() {
				return 0;
			}
		};
	}
}
