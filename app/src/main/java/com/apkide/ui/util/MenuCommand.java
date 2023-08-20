package com.apkide.ui.util;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.apkide.common.command.Command;


public interface MenuCommand extends Command {

	@IdRes
	int getId();

	@DrawableRes
	default int getIcon(){
		return 0;
	}

	@Nullable
	default String getTitle() {
		return null;
	}

	boolean isVisible();
}
