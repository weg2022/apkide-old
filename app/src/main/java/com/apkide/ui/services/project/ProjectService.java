package com.apkide.ui.services.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.apkide.common.AppLog;
import com.apkide.ui.App;

import java.io.File;

public class ProjectService {
	private ProjectModel model;
	private ProjectServiceListener listener;
	private SharedPreferences preferences;

	public ProjectService() {
	}

	public void init() {
		preferences = App.getContext().getSharedPreferences("ProjectService", Context.MODE_PRIVATE);
		String lastProject = preferences.getString("lastProjectRoot", "");
		if (!TextUtils.isEmpty(lastProject) && new File(lastProject, "apktool.yaml").exists()) {
			openProject(lastProject);
		}
	}


	public void openProject(String rootPath) {
		File file = new File(rootPath);
		if (!file.exists() && !file.isDirectory() || !new File(file, "apktool.yaml").exists()) {
			AppLog.e("Error loading project " + rootPath);
			return;
		}

		if (isProjectOpen())
			closeProject();

		model = ProjectManager.createProjectModel(rootPath);
		preferences.edit().putString("rootPath", model.getRootPath()).apply();
		if (listener != null)
			listener.projectOpened(model);
	}


	public boolean isProjectOpen() {
		return model != null;
	}

	public void closeProject() {
		if (model != null) {
			preferences.edit().putString("rootPath", "").apply();
			preferences.edit().putString("lastProjectRoot", model.getRootPath()).apply();
			if (listener != null)
				listener.projectClosed(model);//notify
			model = null;
		}
	}


	public void setListener(ProjectServiceListener listener) {
		this.listener = listener;
	}


}
