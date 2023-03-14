package com.apkide.ui.services.project;

public interface ProjectServiceListener {
	void projectOpened(ProjectModel project);
	
	void projectClosed(ProjectModel project);
}
