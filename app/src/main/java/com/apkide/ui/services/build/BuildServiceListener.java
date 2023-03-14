package com.apkide.ui.services.build;

public interface BuildServiceListener {
	void buildStarted();
	
	void buildProgress(int progress, String taskName, String description);
	
	void buildError(String errorMessage);
	
	void buildFinished();
}
