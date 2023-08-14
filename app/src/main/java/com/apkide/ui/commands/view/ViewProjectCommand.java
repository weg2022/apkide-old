package com.apkide.ui.commands.view;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class ViewProjectCommand implements MenuCommand {
	@Override
	public int getId() {
		return R.id.commandViewError;
	}

	@Override
	public boolean isVisible() {
		return false;
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
