package com.apkide.ui.services.project;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.common.FileSystem;

import java.io.File;

public class TestProjectManager implements ProjectManager {
	@NonNull
	@Override
	public String getName() {
		return "Test";
	}

	@NonNull
	@Override
	public String[] getSupportedLanguages() {
		return new String[]{"Java", "XML"};
	}

	@Override
	public boolean isSupportedProject(@NonNull String rootPath) {

		if (new File(rootPath, "build.gradle").exists() &&
				new File(FileSystem.getParentDirPath(rootPath), "build.gradle").exists()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isProjectFile(@NonNull String path) {
		String appDir = FileSystem.getEnclosingParent(path, "app");
		if (appDir == null) return false;

		if (new File(appDir, "build.gradle").exists() &&
				new File(FileSystem.getParentDirPath(appDir), "build.gradle").exists()) {
			return true;
		}

		return false;
	}

	private String path;

	@Override
	public void open(@NonNull String rootPath) {
		path = rootPath;
		AppLog.s(this, "open: " + path);
	}

	@Override
	public void close() {
		AppLog.s(this, "close: " + path);
	}

	@Override
	public void sync() {

	}
}
