package com.apkide.ui.commands;

import androidx.annotation.IdRes;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.preferences.PreferencesUI;
import com.apkide.ui.util.MenuCommand;

public class GotoSettingsCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandGotoSettings;
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
		App.gotoUI(PreferencesUI.class.getName());
		return true;
	}
}
