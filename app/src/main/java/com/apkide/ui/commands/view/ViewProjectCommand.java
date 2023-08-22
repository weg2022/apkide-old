package com.apkide.ui.commands.view;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.BrowserPager;
import com.apkide.ui.util.MenuCommand;

public class ViewProjectCommand implements MenuCommand {
	@Override
	public int getId() {
		return R.id.commandViewProject;
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
		if (App.getMainUI().getBrowserPager().getIndex()!= BrowserPager.PROJECT_BROWSER){
			App.getMainUI().getBrowserPager().toggle(BrowserPager.PROJECT_BROWSER);
		}
		return true;
	}
}
