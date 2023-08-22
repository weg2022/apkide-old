package com.apkide.ui.commands.actionbar;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.apkide.apktool.engine.service.IProcessingCallback;
import com.apkide.common.AppLog;
import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

import java.io.File;

public class RunCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandRun;
	}

	@Override
	public boolean isVisible() {
		return App.getProjectService().isProjectOpened();
	}

	@Override
	public boolean isEnabled() {
		return App.getProjectService().isProjectOpened();
	}

	@Override
	public boolean run() {
		App.getApkEngineService().buildApk(App.getProjectService().getProjectRootPath(),
				App.getProjectService().getProjectRootPath() + File.separator + "build", new IProcessingCallback() {
					@Override
					public void processPrepare(@NonNull String sourcePath) {
						AppLog.s("build started");
					}
					
					@Override
					public void processing(@NonNull String msg) {
						AppLog.s(msg);
					}
					
					@Override
					public void processError(@NonNull Throwable error) {
						AppLog.e(error);
					}
					
					@Override
					public void processDone(@NonNull String outputPath) {
						AppLog.s("build done");
					}
				});
		return true;
	}
}
