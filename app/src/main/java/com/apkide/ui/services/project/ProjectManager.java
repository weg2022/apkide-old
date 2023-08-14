package com.apkide.ui.services.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ProjectManager {

	@NonNull
	String getName();

	@NonNull
	String[] getSupportedLanguages();

	boolean checkIsSupportedProjectRootPath(@NonNull String rootPath);

	boolean checkIsSupportedProjectPath(@NonNull String path);

	void open(@NonNull String rootPath);

	void close();

	void sync();

	boolean isOpen();

	@Nullable
	String getRootPath();

	boolean isProjectFilePath(@NonNull String path);

	@Nullable
	String resolvePath(@NonNull String path);

	int getIcon();
}
