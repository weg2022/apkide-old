package com.apkide.ui.commands.view;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

public class ViewFind implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionViewFind;
    }

    @NonNull
    @Override
    public String getName() {
        return "Find";
    }

    @Override
    public boolean commandPerformed() {
        App.getMainUI().toggleFindBrowser();
        return true;
    }
}
