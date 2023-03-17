package com.apkide.ui.services.build;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuildService {

	private BuildServiceListener listener;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();


	public void setListener(BuildServiceListener listener) {
		this.listener = listener;
	}
}
