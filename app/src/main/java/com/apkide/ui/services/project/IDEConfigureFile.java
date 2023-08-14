package com.apkide.ui.services.project;

import androidx.annotation.NonNull;

public class IDEConfigureFile extends ConfigureFile {


	private final String myApkToolFilePath;

	public IDEConfigureFile(@NonNull String devFilePath, String apktoolFilePath) {
		super(devFilePath);
		myApkToolFilePath = apktoolFilePath;
	}

	public String getApkToolFilePath() {
		return myApkToolFilePath;
	}

	@Override
	protected void onSync() throws ConfigureFileException {



	}

	@Override
	protected void onDestroy() {

	}
}
