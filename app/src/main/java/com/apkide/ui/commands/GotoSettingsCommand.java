package com.apkide.ui.commands;

import android.content.Intent;

import androidx.annotation.IdRes;

import com.apkide.ui.App;
import com.apkide.ui.R;
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
		Intent intent = new Intent();
		intent.setClassName("com.apkide.ui",
				"com.apkide.ui.preferences.PreferencesUI");
		App.getMainUI().startActivity(intent);
		return true;
	}
}
