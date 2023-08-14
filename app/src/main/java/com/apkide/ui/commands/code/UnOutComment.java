package com.apkide.ui.commands.code;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class UnOutComment implements MenuCommand {
	@Override
	public int getId() {
		return R.id.commandUnOutComment;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public boolean run() {
		return false;
	}
}
