package com.apkide.ui.commands;

import androidx.annotation.IdRes;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class SaveAllCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandSaveAll;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return App.getOpenFileService().isOpen();
	}

	@Override
	public boolean run() {
		App.getOpenFileService().saveAll();
		return true;
	}
}
