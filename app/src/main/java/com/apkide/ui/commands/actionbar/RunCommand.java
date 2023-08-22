package com.apkide.ui.commands.actionbar;

import androidx.annotation.IdRes;

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
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean run() {
		return true;
	}
}
