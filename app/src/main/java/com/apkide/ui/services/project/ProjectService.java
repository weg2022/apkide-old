package com.apkide.ui.services.project;

public class ProjectService {
	private ProjectModel model;
	private ProjectServiceListener listener;
	
	
	public ProjectService() {
	
	}
	
	
	public void setListener(ProjectServiceListener listener) {
		this.listener = listener;
	}
	
	
}
