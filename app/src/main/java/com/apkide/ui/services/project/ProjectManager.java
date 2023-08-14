package com.apkide.ui.services.project;

import androidx.annotation.NonNull;

public interface ProjectManager {

	@NonNull
	String getName();

	@NonNull
	String[] getSupportedLanguages();

	boolean isSupportedProject(@NonNull String rootPath);

	boolean isProjectFile(@NonNull String path);

	void open(@NonNull String rootPath);

	void close();

	void sync();
}
