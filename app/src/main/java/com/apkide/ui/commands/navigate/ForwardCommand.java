package com.apkide.ui.commands.navigate;

import androidx.annotation.IdRes;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class ForwardCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandForward;
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
