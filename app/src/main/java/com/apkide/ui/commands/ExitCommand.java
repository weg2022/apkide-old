package com.apkide.ui.commands;

import com.apkide.ui.AppCommands;
import com.apkide.ui.R;

public class ExitCommand implements AppCommands.MenuCommand {
	@Override
	public int getId() {
		return R.id.mainActionExit;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean run() {
		return false;
	}
}
