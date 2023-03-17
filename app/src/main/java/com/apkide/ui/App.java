package com.apkide.ui;

import android.content.Context;
import android.os.Handler;

import com.apkide.ui.services.build.BuildService;
import com.apkide.ui.services.file.OpenFileService;
import com.apkide.ui.services.project.ProjectService;
import com.apkide.ui.services.scm.GitService;

public final class App {

	private static Context context;
	private static App app;
	private static Handler handler;
	private static MainUI mainUI;

	private final OpenFileService openFileService = new OpenFileService();
	private final ProjectService projectService = new ProjectService();
	private final BuildService buildService = new BuildService();
	private final GitService gitService = new GitService();

	private App() {
	}

	public static void initApp(Context context) {
		App.context = context;
	}

	public static Context getContext() {
		return context;
	}

	public static void init(MainUI mainUI) {
		app = new App();
		App.mainUI = mainUI;
		App.handler = new Handler();
		initServices();
	}

	private static void initServices() {
		app.projectService.init();
	}

	public static void shutdown() {
		if (app != null) {


			app = null;
		}
	}

	public static OpenFileService getOpenFileService() {
		return app.openFileService;
	}

	public static ProjectService getProjectService() {
		return app.projectService;
	}

	public static BuildService getBuildService() {
		return app.buildService;
	}

	public static GitService getGitService() {
		return app.gitService;
	}

	public static boolean isShutdown() {
		return app != null;
	}


}
