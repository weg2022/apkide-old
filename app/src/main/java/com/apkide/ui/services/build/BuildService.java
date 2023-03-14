package com.apkide.ui.services.build;

public class BuildService {
	
	private BuildServiceListener listener;
	
	
	public void setListener(BuildServiceListener listener) {
		this.listener = listener;
	}
}
