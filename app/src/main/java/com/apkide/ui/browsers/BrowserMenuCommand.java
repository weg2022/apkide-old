package com.apkide.ui.browsers;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.apkide.common.Command;


public interface BrowserMenuCommand extends Command {

	@DrawableRes
	int getIcon();

	@StringRes
	int getLabel();

	boolean isVisible();

}
