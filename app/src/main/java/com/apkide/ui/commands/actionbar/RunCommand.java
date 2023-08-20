package com.apkide.ui.commands.actionbar;

import androidx.annotation.IdRes;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

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
		return true;
	}
}
