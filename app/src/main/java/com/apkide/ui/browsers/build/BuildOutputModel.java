package com.apkide.ui.browsers.build;

import com.apkide.ui.views.CodeEditTextModel;

public class BuildOutputModel extends CodeEditTextModel {
    public BuildOutputModel() {
        super();
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public boolean isLogging() {
        return true;
    }
    
    @Override
    public boolean isLogErrorLine(int line) {
        return super.isLogErrorLine(line);
    }
}
