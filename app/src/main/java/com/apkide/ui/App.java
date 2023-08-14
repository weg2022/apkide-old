package com.apkide.ui;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.apkide.common.Application;
import com.apkide.ui.browsers.file.FileBrowserService;
import com.apkide.ui.services.openfile.OpenFileService;
import com.apkide.ui.services.project.ProjectService;

import java.util.ArrayList;
import java.util.List;

public final class App {

	private static final List<StyledUI> sActivities = new ArrayList<>();
	private static App sApp;
	private static Handler sHandler;
	private static MainUI sMainUI;

	private  final FileBrowserService myFileBrowserService=new FileBrowserService();
	private final OpenFileService myOpenFileService=new OpenFileService();
	private final ProjectService myProjectService=new ProjectService();
	private App() {
	}

	public static FileBrowserService getFileBrowserService(){
		return sApp.myFileBrowserService;
	}

	public static OpenFileService getOpenFileService(){
		return sApp.myOpenFileService;
	}

	public static ProjectService getProjectService(){
		return sApp.myProjectService;
	}

	public static void init(@NonNull MainUI mainUI) {
		sApp = new App();
		sHandler = new Handler(requireNonNull(Looper.myLooper()));
		sMainUI = mainUI;
		sApp.myFileBrowserService.initialize();
		sApp.myOpenFileService.initialize();
		sApp.myProjectService.initialize();
	}


	public static void shutdown() {
		if (sApp != null) {

			sApp.myFileBrowserService.shutdown();
			sApp.myOpenFileService.shutdown();
			sApp.myProjectService.shutdown();
			sActivities.clear();
			sMainUI = null;
			sHandler = null;
			sApp = null;
		}
	}

	public static boolean isShutdown() {
		return sApp == null;
	}

	public static boolean postRun(@NonNull Runnable runnable) {
		return sHandler.post(() -> {
			if (isShutdown())
				return;
			runnable.run();
		});
	}

	public static boolean postRun(@NonNull Runnable runnable, long delayMillis) {
		return sHandler.postDelayed(() -> {
			if (isShutdown())
				return;
			runnable.run();
		}, delayMillis);
	}

	public static MainUI getMainUI() {
		return sMainUI;
	}

	@NonNull
	public static StyledUI getUI() {
		return sActivities.isEmpty() ? sMainUI : sActivities.get(sActivities.size() - 1);
	}

	public static void startUI(@NonNull StyledUI ui) {
		int index = sActivities.indexOf(ui);
		if (index != -1)
			sActivities.remove(index);

		sActivities.add(ui);
	}

	public static void stopUI(@NonNull StyledUI ui) {
		sActivities.remove(ui);
	}

	public static void runOnBackground(@NonNull Runnable backgroundRun) {
		new Thread(backgroundRun).start();
	}

	public static void runOnBackground(@NonNull Runnable backgroundRun, @Nullable Runnable uiRun) {
		new Thread(() -> {
			backgroundRun.run();
			postRun(() -> {
				if (uiRun != null)
					uiRun.run();
			});
		}).start();
	}

	@NonNull
	public static Context getContext() {
		if (sMainUI != null) {
			return sMainUI;
		}
		if (sActivities.isEmpty()) {
			return Application.get().getContext();
		}
		return sActivities.get(sActivities.size() - 1);
	}


	@NonNull
	public static String getString(@StringRes int id) {
		return getContext().getString(id);
	}

	@NonNull
	public static SharedPreferences getPreferences(@NonNull String name) {
		return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
	}

}
