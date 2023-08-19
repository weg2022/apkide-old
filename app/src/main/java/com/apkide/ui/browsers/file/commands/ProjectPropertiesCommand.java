package com.apkide.ui.browsers.file.commands;

import com.apkide.ui.R;
import com.apkide.ui.browsers.BrowserMenuCommand;

public class ProjectPropertiesCommand implements BrowserMenuCommand {
    @Override
    public int getIcon() {
        return R.drawable.project_properties;
    }
    
    @Override
    public int getLabel() {
        return R.string.browser_file_project_properties;
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
        return false;
    }
}
