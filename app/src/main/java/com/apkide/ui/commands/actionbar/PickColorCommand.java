package com.apkide.ui.commands.actionbar;

import androidx.annotation.IdRes;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class PickColorCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandPickColor;
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
