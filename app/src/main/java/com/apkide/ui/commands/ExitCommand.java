package com.apkide.ui.commands;

import androidx.annotation.IdRes;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class ExitCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandExit;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean run() {
		App.getMainUI().shutdown();
		return true;
	}
}
