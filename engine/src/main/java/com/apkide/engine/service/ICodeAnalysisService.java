package com.apkide.engine.service;

public interface ICodeAnalysisService {



	void initialize();

	void shutdown();

	void openFile(String filePath);

	void closeFile(String filePath);

	void setHighlightingListener(IHighlightingListener listener);

}
