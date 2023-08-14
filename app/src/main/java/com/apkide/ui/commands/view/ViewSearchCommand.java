package com.apkide.ui.commands.view;

import androidx.annotation.IdRes;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.BrowserPager;
import com.apkide.ui.util.MenuCommand;

public class ViewSearchCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandViewSearch;
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
		if (App.getMainUI().getBrowserPager().getIndex()!=BrowserPager.SEARCH_BROWSER){
			App.getMainUI().getBrowserPager().toggle(BrowserPager.SEARCH_BROWSER);
		}
		return true;
	}
}
