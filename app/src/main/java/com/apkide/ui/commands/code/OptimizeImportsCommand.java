package com.apkide.ui.commands.code;

import androidx.annotation.IdRes;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class OptimizeImportsCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandOptimizeImports;
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