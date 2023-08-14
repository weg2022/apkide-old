package com.apkide.ui.commands.view;

import androidx.annotation.IdRes;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.BrowserPager;
import com.apkide.ui.util.MenuCommand;

public class ViewErrorCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandViewError;
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
		if (!App.getMainUI().getSplitLayout().isSplit()){
			App.getMainUI().getSplitLayout().openSplit();
		}
		if (App.getMainUI().getBrowserPager().getIndex()!= BrowserPager.ERROR_BROWSER){
			App.getMainUI().getBrowserPager().toggle(BrowserPager.ERROR_BROWSER);
		}
		return true;
	}
}
